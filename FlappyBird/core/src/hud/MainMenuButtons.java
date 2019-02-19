package hud;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fungames.flappybird.GameMain;

import Helpers.GameInfo;
import Helpers.GameManager;
import Sences.Gameplay;

public class MainMenuButtons {

    private GameMain game;

    private Stage stage;
    private Viewport gameViewport;

    private ImageButton playBtn, scoreBtn,changeBirdBtn;

    public MainMenuButtons(GameMain game){

        this.game = game;

        gameViewport = new FitViewport(GameInfo.WIDTH,GameInfo.HEIGHT,new OrthographicCamera());

        stage = new Stage(gameViewport,game.getBatch());

        createAndPositionButtons();

        stage.addActor(playBtn);
        stage.addActor(scoreBtn);

        changeBird();

    }

    void createAndPositionButtons(){

        playBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Buttons/play.png"))));

        scoreBtn = new ImageButton(new SpriteDrawable(new Sprite( new Texture("Buttons/Score.png"))));

        playBtn.setPosition(GameInfo.WIDTH/2f - 100, GameInfo.HEIGHT/2f-30, Align.center);

        scoreBtn.setPosition(GameInfo.WIDTH/2f + 100, GameInfo.HEIGHT/2f-30, Align.center);

        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new Gameplay(game));
                stage.dispose();
            }
        });
    }

    void changeBird(){

        if(changeBirdBtn != null){
            changeBirdBtn.remove();
        }

        changeBirdBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Birds/"
                + GameManager.getInstance().getBird()+"/Idle.png"))));
        changeBirdBtn.setPosition(GameInfo.WIDTH/2f, GameInfo.HEIGHT/2f+175,Align.center);

        changeBirdBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameManager.getInstance().incrementIndex();

                //call change bird to change the bird
                changeBird();
            }
        });

        stage.addActor(changeBirdBtn);
    }

    public Stage getStage(){
        return this.stage;
    }


} // main menu buttons
