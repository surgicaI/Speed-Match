package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class MyFirstGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
    Texture background;
	OrthographicCamera camera;
    Array<Card> mCards;
    float center_x;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,1440,2560);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		img = new Texture("myImg.png");
        background = new Texture("bg_3.jpg");
        mCards = new Array<Card>();
        center_x = camera.viewportWidth/2;
        mCards.add(new Card(center_x,camera.viewportWidth/2,camera.viewportHeight/2));
        mCards.add(new Card(center_x,camera.viewportWidth*3/2,camera.viewportHeight/2));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        camera.update();
		batch.begin();
        batch.draw(background,camera.position.x - background.getWidth()/2,camera.position.y - background.getHeight()/2);
        updateCards();
        renderCards();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

    public void updateCards(){
        for(Card card:mCards){
            card.update(camera,Gdx.graphics.getDeltaTime());
        }
    }

    public void renderCards(){
        for(Card card:mCards){
            card.render(batch);
        }
    }

}
