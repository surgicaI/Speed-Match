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
    public static final int STATE_GAME_INIT = 0;
    public static final float SINGLE_GAME_TIME = 60f;
    public static final float GAME_INIT_TIME = 1.5f;
    public static final float HELP_SHOW_TIME = 8f;
	SpriteBatch batch;
    Texture background;
	OrthographicCamera camera;
    Array<Card> mCards;
    float center_x;
    boolean cardsMoving = false;
    boolean isTouched;
    int previousId, currentId ;
    int score;
    StringBuilder scoreStringBuilder,timerStringBuilder;
    BitmapFont font, scoreMultiplierFont, finalScoreFont;
    Texture tick, cross;
    float timeForTick = 0;
    float timeForCross = 0;
    float tickCrossPosition_x,tickCrossPosition_y;
    int scoreMultiplier = 0;
	Texture match_button, do_not_match_button,play_button, help_button,logo;
    Rectangle matchRect, doNotMatchRect,playRect,helpRect;
    int gameState;
    float timer,timerWidth, helpTimer, gameInitTimer;
    int[] scoreHeightWidthTuple;
    float scoreMultiplierWidth,scoreMultiplierHeight,rememberStringWidth, alwaysInstructionStringWidth;
    String scoreMultiplierString, rememberString, alwaysInstructionString;
    String[] instructions = new String[4];
    float[] instruction_width = new float[4];
    float logoHeight,logo_position_y;
	@Override
	public void create () {
        score = 0;
        scoreStringBuilder = new StringBuilder();
        timerStringBuilder = new StringBuilder();
        scoreStringBuilder.append("Score: 0");
        timerStringBuilder.append("Time 00:00");
        font = new BitmapFont(Gdx.files.internal("myFont.fnt"));
        GlyphLayout glyphLayout = new GlyphLayout(font, timerStringBuilder.toString());
        timerWidth = glyphLayout.width;
        scoreMultiplierFont = new BitmapFont(Gdx.files.internal("score_font.fnt"));
        finalScoreFont = new BitmapFont(Gdx.files.internal("final_score_font.fnt"));
        finalScoreFont.getData().setScale(0.6f);
        font.getData().setScale(1f);
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,1440,2560);
		Gdx.gl.glClearColor(1, 1, 1, 1);
        background = new Texture("bg.jpg");
        tick = new Texture("tick.png");
        cross = new Texture("cross.png");
        match_button = new Texture("match_button.png");
        do_not_match_button = new Texture("do_not_match_button.png");
        play_button = new Texture("play_button.png");
        help_button = new Texture("help_button.png");
        logo = new Texture("logo.png");
        mCards = new Array<Card>();
        center_x = camera.viewportWidth/2;
        mCards.add(new Card(center_x,camera.viewportWidth/2,camera.viewportHeight/2));
        mCards.add(new Card(center_x,camera.viewportWidth*3/2,camera.viewportHeight/2));
        tickCrossPosition_x = Card.selectedCardPosition_x+Card.mCardWidth/2-cross.getWidth()/2;
        tickCrossPosition_y = Card.selectedCardPosition_y+Card.mCardHeight+cross.getHeight()/4;
        doNotMatchRect = new Rectangle(10,20,camera.viewportWidth/2-20,200);
        matchRect = new Rectangle(camera.viewportWidth/2+10,20,camera.viewportWidth/2-20,200);
        playRect = new Rectangle(camera.viewportWidth/4,Card.selectedCardPosition_y-3*play_button.getHeight()/2,camera.viewportWidth/2,200);
        helpRect = new Rectangle(camera.viewportWidth/4,playRect.getY()-250,camera.viewportWidth/2,200);
        gameState = STATE_STOPPED;
        timer = 0f;
        scoreHeightWidthTuple = new int[3];
        initAllStrings();
        helpTimer = 0f;
        gameInitTimer = GAME_INIT_TIME;
        logoHeight = 450;
        logo_position_y = Card.selectedCardPosition_y + Card.mCardHeight;
        logo_position_y = logo_position_y + (camera.viewportHeight-logo_position_y-logoHeight)/2;
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        camera.update();
		batch.begin();
        batch.draw(background,camera.position.x - background.getWidth()/2,camera.position.y - background.getHeight()/2);
        if(gameState==STATE_STOPPED && Gdx.input.justTouched()){
            Vector2 touchPoint = new Vector2(Gdx.input.getX()*camera.viewportWidth/Gdx.graphics.getWidth(),camera.viewportHeight-Gdx.input.getY()*camera.viewportHeight/Gdx.graphics.getHeight());
            if(playRect.contains(touchPoint)) {
                Gdx.input.vibrate(5);
                initGame();
            }else if(helpRect.contains(touchPoint)){
                Gdx.input.vibrate(5);
                helpTimer = HELP_SHOW_TIME;
            }
        }
        if(gameState==STATE_GAME_INIT) {
            gameInitTimer -= Gdx.graphics.getDeltaTime();
            handleInitState();
            if(gameInitTimer<=0){
                previousId = Card.selectedCardImageId;
                isTouched = true;
                updateCards();
                isTouched = false;
                startGame();
            }
        }else if(gameState==STATE_GAME_ON) {
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
                drawAlwaysInstructionString();
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
        background.dispose();
        do_not_match_button.dispose();
        match_button.dispose();
        play_button.dispose();
        help_button.dispose();
        logo.dispose();
        font.dispose(); ;
        scoreMultiplierFont.dispose();
        finalScoreFont.dispose();
        tick.dispose();
        cross.dispose();
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
            if(Gdx.input.getX()*camera.viewportWidth/Gdx.graphics.getWidth()>=camera.viewportWidth/2){
                correct();
            }else{
                wrong();
            }
        }else{
            if(Gdx.input.getX()*camera.viewportWidth/Gdx.graphics.getWidth()>camera.viewportWidth/2){
                wrong();
            }else{
                correct();
            }
        }
        previousId = currentId;
    }

    public void correct(){
        if(scoreMultiplier<50) scoreMultiplier += 5;
        score += scoreMultiplier;
        scoreStringBuilder.replace(7,scoreStringBuilder.length, Integer.toString(score));
        timeForTick = 0.4f;
        scoreMultiplierString = "+"+scoreMultiplier;
        GlyphLayout glyphLayout = new GlyphLayout(scoreMultiplierFont,scoreMultiplierString);
        scoreMultiplierWidth = glyphLayout.width;
        scoreMultiplierHeight = glyphLayout.height;
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
            scoreMultiplierFont.draw(batch, scoreMultiplierString,(camera.viewportWidth-scoreMultiplierWidth)/2,Card.selectedCardPosition_y-scoreMultiplierHeight);
        }else if(timeForCross > 0){
            timeForCross -= Gdx.graphics.getDeltaTime();
            batch.draw(cross,tickCrossPosition_x,tickCrossPosition_y);
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
        Vector2 touchPoint = new Vector2(Gdx.input.getX()*camera.viewportWidth/Gdx.graphics.getWidth(),camera.viewportHeight-Gdx.input.getY()*camera.viewportHeight/Gdx.graphics.getHeight());
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
        timeForCross = 0;
        timeForTick = 0;
        score = 0;
        scoreStringBuilder.replace(7,scoreStringBuilder.length, Integer.toString(score));
    }
    public void gameOver(){
        float x_pos = camera.viewportWidth/2;
        for(Card card:mCards){
            card.reset(center_x,x_pos,camera.viewportHeight/2);
            x_pos += camera.viewportWidth;
        }
        scoreMultiplier = 0;
        helpTimer = 0f;
    }
    public void updateScoreAndTimer(){
        font.draw(batch, scoreStringBuilder.toString(), SCORE_POSITION, camera.viewportHeight - SCORE_POSITION);
        font.draw(batch, timerStringBuilder.toString(), camera.viewportWidth-SCORE_POSITION-timerWidth, camera.viewportHeight - SCORE_POSITION);
    }

    public void handleStoppedState(){
        if(score!=0){
            if(scoreHeightWidthTuple[0]!=score){
                scoreHeightWidthTuple[0]=score;
                GlyphLayout glyphLayout = new GlyphLayout(finalScoreFont, "Your Score: "+score);
                scoreHeightWidthTuple[1] = (int)glyphLayout.width;
                scoreHeightWidthTuple[2] = (int)glyphLayout.height;
            }
            finalScoreFont.draw(batch,"Your Score: "+score,(camera.viewportWidth- scoreHeightWidthTuple[1])/2,Card.selectedCardPosition_y+Card.mCardHeight+3*scoreHeightWidthTuple[2]/2);
        }
        batch.draw(logo,25,logo_position_y,camera.viewportWidth-50,logoHeight);
        batch.draw(play_button,playRect.getX(),playRect.getY(),playRect.getWidth(),playRect.getHeight());
        if(helpTimer==0f) {
            batch.draw(help_button, helpRect.getX(), helpRect.getY(), helpRect.getWidth(), helpRect.getHeight());
        }else {
            helpTimer -= Gdx.graphics.getDeltaTime();
            showHelp();
            if(helpTimer<=0f) helpTimer = 0f;
        }
    }

    public void initAllStrings(){
        GlyphLayout glyphLayout = new GlyphLayout();
        instructions[0] = "Instructions";
        instructions[1] = "\nIf the current card matches previous";
        instructions[2] = "\n\ncard tap YES, otherwise tap NO.";
        instructions[3] = "\n\n\nYou have 1 minute. GO!";
        rememberString = "Remember the card" ;
        alwaysInstructionString = "Does the card matches previous card?";
        int index = 0;
        for(String instruction: instructions) {
            glyphLayout.reset();
            glyphLayout.setText(font,instruction);
            instruction_width[index] = glyphLayout.width;
            index++;
        }
        glyphLayout.reset();
        glyphLayout.setText(font,rememberString);
        rememberStringWidth = glyphLayout.width;
        glyphLayout.reset();
        glyphLayout.setText(font,alwaysInstructionString);
        alwaysInstructionStringWidth = glyphLayout.width;
    }

    public void showHelp(){
        for(int index = 0;index<4;index++){
            font.draw(batch,instructions[index],(camera.viewportWidth-instruction_width[index])/2,playRect.getY()-50);
        }
    }

    public void initGame(){
        gameState = STATE_GAME_INIT;
        gameInitTimer = GAME_INIT_TIME;
    }

    public void handleInitState(){
        font.draw(batch,rememberString,(camera.viewportWidth-rememberStringWidth)/2,playRect.getY()-50);
    }

    public void drawAlwaysInstructionString(){
        font.draw(batch,alwaysInstructionString,(camera.viewportWidth-alwaysInstructionStringWidth)/2,matchRect.getY()+matchRect.getHeight()+200);
    }

}
