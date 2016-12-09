package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyFirstGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	OrthographicCamera camera;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(true, 720, 720);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		img = new Texture("myImg.png");
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		int point[] = update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, point[0], point[1]);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public int[] update(){
		int[] point = new int[2];
		point[0] = Gdx.input.getX() -64 ;
		point[1] = Gdx.input.getY() -64 ;
		return  point;
	}
}
