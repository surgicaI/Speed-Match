package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by simranjyotsingh on 08/12/16.
 */

public class Card {
    public static final int MAX_IMAGES = 4;
    public static final int MAX_VELOCITY_X = -6000;
    public static int selectedCardImageId = 0;
    public static int nonSelectedCardImageId = 0;
    public static float mCardHeight = mCardHeight = Gdx.graphics.getHeight()/3f;;
    public static float mCardWidth = mCardWidth = mCardHeight*0.9f ;;
    public float position_x, position_y;
    public static float selectedCardPosition_x,selectedCardPosition_y ;
    Sprite card;
    int velocity_x = 0;
    boolean isSelected = false;
    int imageId;

    public Card(float center_x,float x, float y){
        reset(center_x,x,y);
    }

    public void update(boolean isTouched, OrthographicCamera camera, float delta){
        if(isTouched){
            velocity_x = MAX_VELOCITY_X;
        }
        if(position_x+card.getWidth() < 0){
            isSelected = false;
            velocity_x = 0;
            position_x = 3*camera.viewportWidth/2 - card.getWidth()/2;
            randomise();
            nonSelectedCardImageId = imageId;
        }
        if(!isSelected && position_x-50+card.getWidth()/2<=camera.viewportWidth/2){
            velocity_x = 0;
            isSelected = true;
            selectedCardImageId = imageId;
            position_x = camera.viewportWidth/2 - card.getWidth()/2;
        }
        position_x = position_x + velocity_x * delta;
    }
    public void render(SpriteBatch batch){
        card.setPosition(position_x,position_y);
        card.draw(batch);
    }

    public void randomise(){
        float sameOrNot = MathUtils.random();
        if(sameOrNot>=0.5f){
            imageId = nonSelectedCardImageId==0 ? 1 : nonSelectedCardImageId;
        }else{
            imageId = MathUtils.random(1,MAX_IMAGES-1);
            if(imageId>=nonSelectedCardImageId)
                imageId++;
        }

        String image_path = "card_"+ imageId + ".png";
        card = new Sprite(new Texture(image_path));
        card.setSize(mCardWidth,mCardHeight);
    }

    public boolean isMoving(){
        return velocity_x!=0;
    }

    public void reset(float center_x,float x, float y){
        randomise();
        position_x = x - card.getWidth()/2 ;
        position_y = y - card.getHeight()/2 ;
        if(x==center_x){
            isSelected = true;
            selectedCardImageId = imageId;
            selectedCardPosition_x = position_x;
            selectedCardPosition_y = position_y;
        }else{
            isSelected = false;
            nonSelectedCardImageId = imageId;
        }
        velocity_x = 0;
    }
}
