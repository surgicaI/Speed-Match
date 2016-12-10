package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;

public class MyFirstGame extends ApplicationAdapter {
    public static final int SCORE_POSITION = 50;
    public static final int STATE_STOPPED = -1;
    public static final int STATE_GAME_ON = 1;
    public static final float SINGLE_GAME_TIME = 60f;
	SpriteBatch batch;
	Texture img;
    Texture background;
	OrthographicCamera camera;
    Array<Card> mCards;
    float center_x;
    boolean cardsMoving = false;
    boolean isTouched;
    int previousId, currentId ;
    int score;
    StringBuilder scoreStringBuilder,timerStringBuilder;
    BitmapFont font, scoreMultiplierFont;
    Texture tick, cross;
    float timeForTick = 0;
    float timeForCross = 0;
    float tickCrossPosition_x,tickCrossPosition_y;
    int scoreMultiplier = 0;
    GlyphLayout layout;
	Texture match_button, do_not_match_button,play_button;
    Rectangle matchRect, doNotMatchRect,playRect;
    int gameState;
    float timer;
	@Override
	public void create () {
        score = 0;
        scoreStringBuilder = new StringBuilder();
        timerStringBuilder = new StringBuilder();
        scoreStringBuilder.append("Score: 0");
        timerStringBuilder.append("Time 00:00");
        font = new BitmapFont(Gdx.files.internal("myFont.fnt"));
        scoreMultiplierFont = new BitmapFont(Gdx.files.internal("score_font.fnt"));
        font.getData().setScale(1f);
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,1440,2560);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		img = new Texture("myImg.png");
        background = new Texture("bg.jpg");
        tick = new Texture("tick.png");
        cross = new Texture("cross.png");
        match_button = new Texture("match_button.png");
        do_not_match_button = new Texture("do_not_match_button.png");
        play_button = new Texture("play_button.png");
        mCards = new Array<Card>();
        center_x = camera.viewportWidth/2;
        mCards.add(new Card(center_x,camera.viewportWidth/2,camera.viewportHeight/2));
        mCards.add(new Card(center_x,camera.viewportWidth*3/2,camera.viewportHeight/2));
        tickCrossPosition_x = Card.selectedCardPosition_x+Card.mCardWidth/2-cross.getWidth()/2;
        tickCrossPosition_y = Card.selectedCardPosition_y+Card.mCardHeight+cross.getHeight()/4;
        layout = new GlyphLayout();
        doNotMatchRect = new Rectangle(10,20,camera.viewportWidth/2-20,200);
        matchRect = new Rectangle(camera.viewportWidth/2+10,20,camera.viewportWidth/2-20,200);
        playRect = new Rectangle(camera.viewportWidth/4,Card.selectedCardPosition_y-3*play_button.getHeight()/2,camera.viewportWidth/2,200);
        gameState = STATE_STOPPED;
        timer = 0f;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        camera.update();
		batch.begin();
        batch.draw(background,camera.position.x - background.getWidth()/2,camera.position.y - background.getHeight()/2);
        if(gameState==STATE_STOPPED && Gdx.input.justTouched()){
            Vector2 touchPoint = new Vector2(Gdx.input.getX(),camera.viewportHeight-Gdx.input.getY());
            if(playRect.contains(touchPoint)) {
                Gdx.input.vibrate(5);
                startGame();
            }
        }
        if(gameState==STATE_GAME_ON) {
            handleTimer();
            if(gameState==STATE_STOPPED){
                gameOver();
            }else {
                isTouched = isButonPressed();
                if (isTouched) {
                    currentId = Card.selectedCardImageId;
                    verifyResult();
                }
                if (isTouched || cardsMoving) {
                    updateCards();
                }
                updateScoreAndTimer();
                drawTickOrCross();
                drawMatchButtons();
            }
        }else if(gameState==STATE_STOPPED){
            handleStoppedState();
        }
        renderCards();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        img.dispose();
        background.dispose();
        do_not_match_button.dispose();
        match_button.dispose();
        play_button.dispose();
	}

    public void updateCards(){
        for(Card card:mCards){
            card.update(isTouched,camera,Gdx.graphics.getDeltaTime());
        }
    }

    public void renderCards(){
        cardsMoving = false ;
        for(Card card:mCards){
            card.render(batch);
            cardsMoving = cardsMoving || card.isMoving();
        }
    }

    public void verifyResult(){
        boolean matched = previousId==currentId ;
        if(matched){
            if(Gdx.input.getX()>=camera.viewportWidth/2){
                correct();
            }else{
                wrong();
            }
        }else{
            if(Gdx.input.getX()>camera.viewportWidth/2){
                wrong();
            }else{
                correct();
            }
        }
        previousId = currentId;
    }

    public void correct(){
        //System.out.println("correct");
        if(scoreMultiplier<50) scoreMultiplier += 5;
        score += scoreMultiplier;
        scoreStringBuilder.replace(7,scoreStringBuilder.length, Integer.toString(score));
        timeForTick = 0.4f;
    }

    public void wrong(){
        //System.out.println("in_correct");
        timeForCross = 0.4f;
        scoreMultiplier = 0 ;
    }

    public void drawTickOrCross(){
        if(timeForTick>0){
            timeForTick -= Gdx.graphics.getDeltaTime() ;
            batch.draw(tick,tickCrossPosition_x,tickCrossPosition_y);
            layout.setText(font,"+"+scoreMultiplier);
            scoreMultiplierFont.draw(batch, "+"+scoreMultiplier,tickCrossPosition_x,tickCrossPosition_y+3*cross.getHeight()/2);
        }else if(timeForCross > 0){
            timeForCross -= Gdx.graphics.getDeltaTime();
            batch.draw(cross,tickCrossPosition_x,tickCrossPosition_y);
            //layout.setText(font,"+"+scoreMultiplier);
            //font.draw(batch, "+"+scoreMultiplier,camera.viewportWidth/2-layout.width/2,tickCrossPosition_y+3*cross.getHeight()/2);
        }
        if(timeForCross<0) timeForCross = 0;
        if(timeForTick<0) timeForTick = 0;
    }

    public void drawMatchButtons(){
        batch.draw(do_not_match_button,doNotMatchRect.getX(),doNotMatchRect.getY(),doNotMatchRect.getWidth(),doNotMatchRect.getHeight());
        batch.draw(match_button,matchRect.getX(),matchRect.getY(),matchRect.getWidth(),matchRect.getHeight());
    }

    public boolean isButonPressed(){
        if(!Gdx.input.justTouched()) return false;
        Vector2 touchPoint = new Vector2(Gdx.input.getX(),camera.viewportHeight-Gdx.input.getY());
        if(matchRect.contains(touchPoint) || doNotMatchRect.contains(touchPoint)) {
            Gdx.input.vibrate(5);
            return  !cardsMoving;
        }
        return false;
    }

    public void handleTimer(){
        timer -= Gdx.graphics.getDeltaTime();
        int ceilOfTimer = (int)timer + 1;
        if(timer>=0){
            if(ceilOfTimer<10){
                timerStringBuilder.replace(8,9,"0");
                timerStringBuilder.replace(9,timerStringBuilder.length, Integer.toString(ceilOfTimer));
            }else{
                timerStringBuilder.replace(8,timerStringBuilder.length, Integer.toString(ceilOfTimer));
            }
        }
        if(timer<=0){
            timer = 0;
            gameState = STATE_STOPPED;
        }
    }
    public void startGame(){
        gameState = STATE_GAME_ON;
        timer = SINGLE_GAME_TIME;
        score = 0;
        scoreStringBuilder.replace(7,scoreStringBuilder.length, Integer.toString(score));
    }
    public void gameOver(){
        float x_pos = camera.viewportWidth/2;
        for(Card card:mCards){
            card.reset(center_x,x_pos,camera.viewportHeight/2);
            x_pos += camera.viewportWidth;
        }
    }
    public void updateScoreAndTimer(){
        font.draw(batch, scoreStringBuilder.toString(), SCORE_POSITION, camera.viewportHeight - SCORE_POSITION);
        font.draw(batch, timerStringBuilder.toString(), SCORE_POSITION+camera.viewportWidth/2+100, camera.viewportHeight - SCORE_POSITION);
    }

    public void handleStoppedState(){
        if(score!=0){
            scoreMultiplierFont.draw(batch,"Your Score: "+score,40,Card.selectedCardPosition_y+3*Card.mCardHeight/2);
        }
        batch.draw(play_button,playRect.getX(),playRect.getY(),playRect.getWidth(),playRect.getHeight());
    }

}
