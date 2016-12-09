package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

/**
 * Created by simranjyotsingh on 08/12/16.
 */

public class Card {
    public static final int MAX_IMAGES = 4;
    public float mCardHeight,mCardWidth;
    float position_x, position_y;
    Sprite card;
    int velocity_x = 0;
    boolean isSelected = false;
    int imageId;

    public Card(float center_x,float x, float y){
        mCardHeight = Gdx.graphics.getHeight()/3f;
        mCardWidth = mCardHeight*0.9f ;
        randomise();
        position_x = x - card.getWidth()/2 ;
        position_y = y - card.getHeight()/2 ;
        if(x==center_x){
            isSelected = true;
        }
    }

    public void update(OrthographicCamera camera, float delta){
        if(Gdx.input.isTouched()){
            velocity_x = -6000;
        }
        if(position_x+card.getWidth() < 0){
            isSelected = false;
            velocity_x = 0;
            position_x = 3*camera.viewportWidth/2 - card.getWidth()/2;
            randomise();
        }
        if(!isSelected && position_x-50+card.getWidth()/2<=camera.viewportWidth/2){
            velocity_x = 0;
            isSelected = true;
            position_x = camera.viewportWidth/2 - card.getWidth()/2;
        }
        position_x = position_x + velocity_x * delta;
    }
    public void render(SpriteBatch batch){
        card.setPosition(position_x,position_y);
        card.draw(batch);
    }

    public void randomise(){
        Random rand = new Random();
        imageId = 1+rand.nextInt(MAX_IMAGES);
        String image_path = "card_"+ imageId + ".png";
        card = new Sprite(new Texture(image_path));
        card.setSize(mCardWidth,mCardHeight);
    }
    public int getImageId(){
        return imageId;
    }
}
