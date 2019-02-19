package Sences;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.fungames.flappybird.GameMain;

import Bird.Bird;
import Helpers.GameInfo;
import ground.GroundBody;
import hud.UIHud;
import pipes.Pipes;

public class Gameplay implements Screen, ContactListener {


    private GameMain game;
    private World world;

    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    private OrthographicCamera debugCamera;
    private Box2DDebugRenderer debugRenderer;

    private Array<Sprite> backgrounds = new Array<Sprite>();
    private Array<Sprite> grounds = new Array<Sprite>();

    private Bird bird;
    private GroundBody groundBody;

    private UIHud hud;

    private boolean firstTouch;

    private Array<Pipes> pipesArray = new Array<Pipes>();
    private final int DISTANCE_BETWEEN_PIPES = 120;


    public Gameplay (GameMain game){
        this.game = game;

        mainCamera = new OrthographicCamera(GameInfo.WIDTH,GameInfo.HEIGHT);
        mainCamera.position.set( GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f,0);

        gameViewport = new StretchViewport( GameInfo.WIDTH, GameInfo.HEIGHT,mainCamera);

        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM, GameInfo.HEIGHT / GameInfo.PPM);
        debugCamera.position.set( GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);

        debugRenderer = new Box2DDebugRenderer();

        hud = new UIHud(game);

        createBackgrounds();
        createGrounds();

        // true parameter will allow our bodies to sleep.
        // When nothing is affecting our bodies such as gravity or other forces, our bodies can sleep
        // which will lower the calculation and our game will perform better

        world = new World(new Vector2(0,-9.8f),true);
        world.setContactListener(this);

        bird = new Bird(world, GameInfo.WIDTH / 2f - 70, GameInfo.HEIGHT / 2f);

        groundBody = new GroundBody(world,grounds.get(0));



    }

    void checkFirstTouch(){
        if(!firstTouch){
            if(Gdx.input.justTouched()){
                firstTouch = true;
                bird.activateBird();
                createAllPipes();
            }

        }
    }

    void update(float delta){

        checkFirstTouch();

        if(bird.getAlive()){
            moveBackground();
            moveGround();
            birdFlap();
            updatePipes();
            movePipes();
        }


    }

    void createAllPipes(){

        RunnableAction run = new RunnableAction();
        run.setRunnable(new Runnable() {
            @Override
            public void run() {
                createPipes();
            }
        });

        SequenceAction sa = new SequenceAction();
        sa.addAction(Actions.delay(2f));
        sa.addAction(run);

        hud.getStage().addAction(Actions.forever(sa));
    }


    void birdFlap(){

        if(Gdx.input.justTouched()){
            bird.birdFlap();
        }
    }

    void createBackgrounds(){
        for(int i =0; i<3;i++)
        {
            Sprite bg = new Sprite(new Texture("Backgrounds/Day.jpg"));
            bg.setPosition(i*bg.getWidth(),0);
            backgrounds.add(bg);
        }

    }

    void createGrounds(){
        for(int i = 0; i<3; i++)
        {
            Sprite gd = new Sprite(new Texture("Backgrounds/Ground.png"));
            gd.setPosition(i*gd.getWidth(),-gd.getHeight()/2 - 55);
            grounds.add(gd);
        }
    }

    void drawBackground(SpriteBatch batch){
        for(Sprite bg : backgrounds){
            batch.draw(bg,bg.getX(),bg.getY());
        }
    }

    void drawGround(SpriteBatch batch){
        for(Sprite gd : grounds){
            batch.draw(gd,gd.getX(),gd.getY());
        }
    }

    void moveBackground(){

        for(Sprite bg : backgrounds){
            float x1 = bg.getX() - 2f;
            bg.setPosition(x1,bg.getY());

            if(bg.getX() + GameInfo.WIDTH + (bg.getWidth() / 2f)<mainCamera.position.x){

                float x2 = bg.getX() + bg.getWidth() * 3;
                bg.setPosition(x2,bg.getY());
            }
        }
    }

    void moveGround(){
        for(Sprite gd : grounds){
            float x1 = gd.getX() - 1f;
            gd.setPosition(x1,gd.getY());

            if(gd.getX() + GameInfo.WIDTH + (gd.getWidth() / 2f) < mainCamera.position.x){

                float x2 = gd.getX() + gd.getWidth()*3;
                gd.setPosition(x2,gd.getY());
            }
        }
    }

    void createPipes(){
        Pipes p = new Pipes(world,GameInfo.WIDTH + DISTANCE_BETWEEN_PIPES);
        p.setMainCamera(mainCamera);
        pipesArray.add(p);
    }

    void drawPipes(SpriteBatch batch){

        for(Pipes pipe : pipesArray) {
            pipe.drawPipes(batch);
        }
    }

    void updatePipes(){
        for(Pipes pipe : pipesArray){
            pipe.updatePipes();

        }

    }

    void movePipes(){
        for(Pipes pipe : pipesArray){
            pipe.movePipes();
        }
    }

    void stopPipes(){
        for(Pipes pipe : pipesArray){
            pipe.stopPipes();
        }
    }

    void birdDied(){

        bird.setAlive(false);
        bird.birdDied();
        stopPipes();

        hud.getStage().clear();
        hud.showScore();
        hud.createButtons();

        Gdx.input.setInputProcessor(hud.getStage());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();

        drawBackground(game.getBatch());

        drawGround(game.getBatch());

        bird.DrawIdle(game.getBatch());

        drawPipes(game.getBatch());

        game.getBatch().end();

        debugRenderer.render(world,debugCamera.combined);

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();

        bird.updateBird();

        world.step( Gdx.graphics.getDeltaTime(),6,2);



    }

    @Override
    public void resize(int width, int height) {

        gameViewport.update(width,height);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture body1, body2;

        if(contact.getFixtureA().getUserData() == "Bird"){
            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();
        }
        else{
            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();
        }

        if(body1.getUserData() == "Bird" && body2.getUserData() == "Ground"){
            if(bird.getAlive()){
                System.out.println("Bird Died");
                birdDied();
            }
        }

        if(body1.getUserData() == "Bird" && body2.getUserData() == "Pipe"){
            if(bird.getAlive()){
                System.out.println("Bird Died");
                birdDied();
            }
        }

        if(body1.getUserData() == "Bird" && body2.getUserData() == "Score"){
            if(bird.getAlive()){

                hud.incrementScore();

            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
} // game play
