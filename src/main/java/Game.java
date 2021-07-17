import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Game extends GameApplication  {
    //global
    private Entity player;
    private Entity spider;
    private Entity rock;
    private ArrayList<Entity> centipedeArray=new ArrayList<>();
    private ArrayList<Entity> mushroomArray=new ArrayList<>();
    private int collideInt=0;
    private String currentDirect;
    private double fps=.05;
    private int numRight=0;
    private int numLeft=0;
    private int numUp=0;
    private int numDown=0;
    private boolean spiderDead=false;
    private int currentLevel =3;
    private boolean rockBroke=false;
    int screenHeight=800;
    int screenWidth=1300;


    @Override
    protected void initSettings(GameSettings gameSettings) {//set up function
        gameSettings.setWidth(screenWidth);
        gameSettings.setHeight(screenHeight);
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setTitle("Centipede");
    }

    @Override
    protected void initInput(){//input function
        //up
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                if(!(collideInt==1)){
                    if(player.getY()>=screenHeight - (screenHeight / 4)){
                        currentDirect="Up";
                        player.translateY(-5);
                    }
                    collideInt=0;
                }
            }
        }, KeyCode.UP);
        //down
        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                if(!(collideInt==2)){
                    if(player.getBottomY() <= screenHeight-30){
                        currentDirect="Down";
                        player.translateY(5);
                    }
                    collideInt=0;
                }
            }
        }, KeyCode.DOWN);
        //left
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                if(!(collideInt==3)){
                    if(player.getX() > 0){
                        currentDirect="Left";
                        player.translateX(-5);
                    }
                    collideInt=0;
                }
            }
        }, KeyCode.LEFT);
        //right
        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                if(!(collideInt==4)){
                    if(player.getRightX() < screenWidth-player.getHeight()){
                        currentDirect="Right";
                        player.translateX(5);
                    }
                    collideInt=0;
                }
            }
        }, KeyCode.RIGHT);
        //shoot
        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                getGameWorld().spawn("bullet", player.getX(), player.getY());
                getGameWorld().getSingleton(CentipedeType.BULLET);
                play("laserSound.wav");
            }
        }, KeyCode.SPACE);

    }

    public void centipedeMovement(){//centipedes movement
        for (Entity centi : getGameWorld().getEntitiesByType(CentipedeType.CENTIPEDE)) {//loops through all centipedes
            CentipedeComponent comp = centi.getComponent(CentipedeComponent.class);
            comp.stayMove();
            //constrains the centipede on the screen
            if(centi.getY() <= 0 &&(centi.getX()>(screenWidth / 2)+32||centi.getX()<screenWidth / 2)){
                comp.move(Direction.DOWN, 4);
            }
            if(centi.getBottomY() > screenHeight-20){
                comp.move(Direction.UP,4);
            }
            if(centi.getX()<=0) {
                for (int i = 0; i < 8; i++) {
                    comp.move(Direction.DOWN, 4);
                }
                comp.move(Direction.RIGHT, 4);
            }
            if(centi.getRightX()>=screenWidth){
                for(int i=0;i<8;i++){
                    comp.move(Direction.DOWN,4);
                }
                comp.move(Direction.LEFT,4);
            }
            //changes the directin of the centipede animation
            if(comp.getDirection().equals("DOWN")){
                centi.rotateToVector(new Point2D(0,1));
            }else if(comp.getDirection().equals("UP")){
                centi.rotateToVector(new Point2D(0,-1));
            }else if(comp.getDirection().equals("RIGHT")){
                centi.rotateToVector(new Point2D(1,0));
            }else if(comp.getDirection().equals("LEFT")){
                centi.rotateToVector(new Point2D(-1,0));
            }
        }
    }

    public void spiderMovement(){//spiders movement
        for (Entity spider : getGameWorld().getEntitiesByType(CentipedeType.SPIDER)) {
            SpiderComponent comp = spider.getComponent(SpiderComponent.class);
            comp.stayMove();
            if(spider.getX() <= 0){
                comp.move(Direction.RIGHT,2);
            }else if((spider.getY() <= screenHeight - (screenHeight / 4) )){
                comp.move(Direction.DOWN,2);

            }else if(spider.getX()>=screenWidth){
                comp.move(Direction.LEFT,2);
            }else if(spider.getY()>=screenHeight-30){
                comp.move(Direction.UP,2);
            }
        }
    }

    public void changeSpiderMovement(){//changes the direction of the spider
        if(spiderDead) {
            spawn("spider", myRand(5, screenWidth - 50), 650);
            spider = getGameWorld().getSingleton(CentipedeType.SPIDER);
            spiderDead=false;
        }
        for (Entity spider : getGameWorld().getEntitiesByType(CentipedeType.SPIDER)) {
            SpiderComponent comp = spider.getComponent(SpiderComponent.class);
            double direction=Math.random();
            if(direction<.25){
                comp.move(Direction.RIGHT,2);

            }else if(direction>.25 &&direction<.5){
                comp.move(Direction.LEFT,2);

            }else if(direction>.5 &&direction<.75){
                comp.move(Direction.DOWN,2);

            }else if(direction>.75){
                comp.move(Direction.UP,2);
            }
        }
    }

    public void rockMovement(){//rock movement
        if(rock.getY()>screenHeight){
            rockBroke=true;
        }
        if(rockBroke){//creates a new rock if rock is off screen
            createRock(currentLevel);
            rockBroke=false;
        }
    }

    @Override
    protected void initGame(){//creates all of the enities
        getSettings().setGlobalMusicVolume(.5);
        getGameWorld().addEntityFactory(new CentipedeFactory());
        spawn("background", 0, 0);
        spawn("player", getAppWidth()/2, 750);
        player= getGameWorld().getSingleton(CentipedeType.PLAYER);
        spawn("spider", myRand(5, screenWidth - 50), 650);
        spider= getGameWorld().getSingleton(CentipedeType.SPIDER);
        nextLevel(currentLevel);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {//creates score
        vars.put("score", 0);
    }

    @Override
    protected void initUI() {//displays score
        addVarText("score",50,50);
    }

    @Override
    protected void initPhysics() {//collision function
        //centipede and mushroom collision
        PhysicsWorld physicsWorld = getPhysicsWorld();
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.CENTIPEDE,CentipedeType.MUSHROOM) {
            double x=Math.random();
            @Override
            protected void onCollisionBegin(Entity enemy, Entity mushroom) {
                CentipedeComponent comp=enemy.getComponent(CentipedeComponent.class);
                //if mushroom hits a msuhroom
                if(comp.getDirection().equals("RIGHT")){
                    if(numDown<=numUp) {
                        comp.move(Direction.DOWN, 4);
                    }else{
                        comp.move(Direction.UP, 4);
                    }
                }else if(comp.getDirection().equals("LEFT")){
                    if(numUp<=numDown) {
                        comp.move(Direction.UP, 4);
                    }else{
                        comp.move(Direction.DOWN, 4);
                    }
                }else if(comp.getDirection().equals("UP")){
                    if(numRight<=numLeft) {
                        comp.move(Direction.RIGHT, 4);
                    }else{
                        comp.move(Direction.LEFT, 4);
                    }
                }else if(comp.getDirection().equals("DOWN")){
                    if(numLeft<=numRight) {
                        comp.move(Direction.LEFT, 4);
                    }else{
                        comp.move(Direction.RIGHT, 4);
                    }
                }
            }
        });
        //bullet and centipede collision
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.BULLET,CentipedeType.CENTIPEDE) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                //adds 100 points to score
                spawn("scoreText", new SpawnData(enemy.getX(), enemy.getY()).put("text", "+100"));
                inc("score",+100);
                //creates a mushroom
                Entity x = spawn("mushroom", enemy.getX(), enemy.getY());//subtract mushroom height
                enemy.removeFromWorld();
                if(currentLevel==2) {
                    x.getViewComponent().clearChildren();
                    x.getViewComponent().addChild(texture("mushroomLvl2.png"));
                }
                if(currentLevel==3){
                    x.getViewComponent().clearChildren();
                    x.getViewComponent().addChild(texture("mushroomLvl3.png"));
                }
                mushroomArray.add(x);
                //remove centipede from centipedeArray
                for(int i=0;i<centipedeArray.size();i++){
                    if(centipedeArray.get(i)==enemy){
                        if (i+1 < centipedeArray.size()) {
                            centipedeArray.get(i+1).getViewComponent().clearChildren();
                            centipedeArray.get(i+1).getViewComponent().addChild(texture("centipedeHeadLvl1.png").toAnimatedTexture(2,Duration.seconds(1)).play().loop());
                        }
                        centipedeArray.get(i).removeFromWorld();
                        centipedeArray.remove(i);
                        break;

                    }
                }

                if(centipedeArray.size()==0){//move on to next level
                    if(currentLevel<3) {//next level
                        showMessage("Next Level", () -> {
                            currentLevel++;
                            nextLevel(currentLevel);

                        });
                    }else{//if already beat the game
                        showMessage("NICE! You beat the game.", () -> {
                            currentLevel=1;
                            getGameController().startNewGame();
                        });
                    }
                }
                bullet.removeFromWorld();
            }
        });

        //spider and player colliosn
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.PLAYER,CentipedeType.SPIDER) {
            @Override
            protected void onCollisionBegin(Entity play, Entity enemy) {
                play.removeFromWorld();
                //losing message
                showMessage("You Lost", () -> {
                    currentLevel=1;
                    nextLevel(currentLevel);
                    getGameController().startNewGame();
                });
            }
        });
        //spider and mushroom collison
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.SPIDER,CentipedeType.MUSHROOM){
            @Override
            protected void onCollisionBegin(Entity enemy, Entity mushroom){
                SpiderComponent comp=enemy.getComponent(SpiderComponent.class);
                //changes the direction of the spider
                if(comp.getDirection().equals("RIGHT")){
                    if(numDown<=numUp) {
                        comp.move(Direction.DOWN, 2);
                    }else{
                        comp.move(Direction.UP, 2);
                    }
                }else if(comp.getDirection().equals("LEFT")){
                    if(numUp<=numDown) {
                        comp.move(Direction.UP, 2);
                    }else{
                        comp.move(Direction.DOWN, 2);
                    }
                }else if(comp.getDirection().equals("UP")){
                    if(numRight<=numLeft) {
                        comp.move(Direction.RIGHT, 2);
                    }else{
                        comp.move(Direction.LEFT, 2);
                    }
                }else if(comp.getDirection().equals("DOWN")){
                    if(numLeft<=numRight) {
                        comp.move(Direction.LEFT, 2);
                    }else{
                        comp.move(Direction.RIGHT, 2);
                    }
                }
            }
        });

        //player and centipede colliosn
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.PLAYER,CentipedeType.CENTIPEDE) {
            @Override
            protected void onCollisionBegin(Entity play, Entity enemy) {
                play.removeFromWorld();
                //losing message
                showMessage("You Lost", () -> {
                    currentLevel=1;
                    nextLevel(currentLevel);
                    getGameController().startNewGame();
                });
            }
        });
        //mushroom and player collision
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.PLAYER,CentipedeType.MUSHROOM) {
            @Override
            protected void onCollisionBegin(Entity play, Entity mushroom) {
                //makes it so player cannot move in the direction of the mushroom
                if(currentDirect.equals("Up")){
                    collideInt=1;
                }else if(currentDirect.equals("Down")){
                    collideInt=2;
                }else if(currentDirect.equals("Left")){
                    collideInt=3;
                }else if(currentDirect.equals("Right")){
                    collideInt=4;
                }
            }
        });

        //mushroom and bullet colliosn
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.BULLET,CentipedeType.MUSHROOM) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity mushroom) {
                MushroomComponent mush= mushroom.getComponent(MushroomComponent.class);
                int hit=mush.getHit();
                //makes the mushroom smaller
                if (hit == 1) {
                    mushroom.setScaleUniform(.90);
                } else if (hit == 2) {
                    mushroom.setScaleUniform(.80);
                } else if (hit == 3) {
                    mushroom.setScaleUniform(.70);
                }else if(hit==4){
                    mushroom.removeFromWorld();
                    spawn("scoreText", new SpawnData(mushroom.getX(), mushroom.getY()).put("text", "+10"));
                    inc("score",+10);
                }
                bullet.removeFromWorld();
            }

        });

        //spider bullet collision
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.BULLET,CentipedeType.SPIDER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                enemy.removeFromWorld();
                bullet.removeFromWorld();
                spiderDead=true;
                spawn("scoreText", new SpawnData(enemy.getX(), enemy.getY()).put("text", "+50"));
                inc("score",+50);
            }
        });

        //rock bullet collsion
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.BULLET,CentipedeType.ROCK) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                enemy.removeFromWorld();
                bullet.removeFromWorld();
                rockBroke=true;
                spawn("scoreText", new SpawnData(enemy.getX(), enemy.getY()).put("text", "+30"));
                inc("score",+30);
            }
        });

        //player rock colliosn
        physicsWorld.addCollisionHandler(new CollisionHandler(CentipedeType.PLAYER,CentipedeType.ROCK) {
            @Override
            protected void onCollisionBegin(Entity play, Entity enemy) {
                enemy.removeFromWorld();
                player.removeFromWorld();
                showMessage("You Lost", () -> {
                    currentLevel=1;
                    nextLevel(currentLevel);
                    getGameController().startNewGame();
                });
            }
        });
    }

    //makes the level up by and sets it up
    private void nextLevel(int level){
        numDown=0;
        numUp=0;
        numRight=0;
        numLeft=0;
        if(level==1){
            fps=.05;
            //spawn new player
            player.getViewComponent().clearChildren();
            player.getViewComponent().addChild(texture("playerLvl1.png"));

            //spawn new spider
            spider.getViewComponent().clearChildren();
            spider.getViewComponent().addChild(texture("spiderLvl1.png").toAnimatedTexture(3, Duration.seconds(.2)).play().loop());
            //spawn new mushroom
            for (int i = 0; i < mushroomArray.size(); i++) {
                mushroomArray.get(i).removeFromWorld();
            }

            createRock(1);

            mushroomArray.clear();
            createMushroom(1,30);

            //spawn new centipede
            createCentipede(1);

            run(() -> changeSpiderMovement(), Duration.seconds(5));
            run(() -> rockMovement(), Duration.seconds(6));
        }
        if(level==2) {
            fps=.01;
            //spawn new player
            player.getViewComponent().clearChildren();
            player.getViewComponent().addChild(texture("playerLvl2.png"));

            //spawn new spider
            spider.getViewComponent().clearChildren();
            spider.getViewComponent().addChild(texture("spiderLvl2.png").toAnimatedTexture(3, Duration.seconds(.2)).play().loop());
            //spawn rock
            createRock(2);

            //spawn new mushroom
            for (int i = 0; i < mushroomArray.size(); i++) {
                mushroomArray.get(i).removeFromWorld();
            }
            mushroomArray.clear();
            createMushroom(2,30);
            //spawn new centipede
            createCentipede(2);
            run(() -> changeSpiderMovement(), Duration.seconds(3));
            run(() -> rockMovement(), Duration.seconds(3));
        }else if(level==3){
            fps=.001;
            //spawn new player
            player.getViewComponent().clearChildren();
            player.getViewComponent().addChild(texture("playerLvl3.png"));

            //spawn new spider
            spider.getViewComponent().clearChildren();
            spider.getViewComponent().addChild(texture("spiderLvl3.png").toAnimatedTexture(3, Duration.seconds(.2)).play().loop());

            //spawn rock
           // rock.removeFromWorld();
            createRock(3);

            //spawn new mushroom
            for (int i = 0; i < mushroomArray.size(); i++) {
                mushroomArray.get(i).removeFromWorld();
            }
            mushroomArray.clear();
            createMushroom(3,30);

            //spawn new centipede
            createCentipede(3);
            run(() -> changeSpiderMovement(), Duration.seconds(1));
            run(() -> rockMovement(), Duration.seconds(1));

        }
        run(() -> spiderMovement(), Duration.seconds(fps));
        run(() -> centipedeMovement(), Duration.seconds(fps));
    }

    //creates a mushroom
    public void createMushroom(int level,int amt){
        for(int i=0; i<amt;i++) {
            for (int j = 0; j < 1; j++) {
                int randX = myRand(25, screenWidth - 25);
                int randY = i * 20 + 60;
                Entity x;
                x = spawn("mushroom", randX, randY);
                if(level==2) {
                    x.getViewComponent().clearChildren();
                    x.getViewComponent().addChild(texture("mushroomLvl2.png"));
                }
                if(level==3){
                    x.getViewComponent().clearChildren();
                    x.getViewComponent().addChild(texture("mushroomLvl3.png"));
                }
                mushroomArray.add(x);
            }
        }
    }


    //creates a rock
    public void createRock(int level){
        rock = spawn("rock", myRand(30,screenWidth-30), -30);
        rock.rotateBy(270);
        if(level==2) {
            rock.getViewComponent().clearChildren();
            rock.getViewComponent().addChild(texture("rockLvl2.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop());
        }
        if(level==3){
            rock.getViewComponent().clearChildren();
            rock.getViewComponent().addChild(texture("rockLvl3.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop());
        }
    }


    //creates the entire centipede
    public void createCentipede(int level){
        centipedeArray.clear();
        Entity z = spawn("headCentipede", screenWidth / 2, -10-0*50);
        CentipedeComponent headComp = z.getComponent(CentipedeComponent.class);
        centipedeArray.add(z);
        headComp.move(Direction.DOWN, 4);

        if(level==2) {
            z.getViewComponent().clearChildren();
            z.getViewComponent().addChild(texture("centipedeHeadLvl2.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop());

        }
        if(level==3){
            z.getViewComponent().clearChildren();
            z.getViewComponent().addChild(texture("centipedeHeadLvl3.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop());
        }

        for(int i=1;i<13;i++) {
            Entity x = spawn("centipede", screenWidth / 2, -10-i*50);
            if(level==2) {
                x.getViewComponent().clearChildren();
                x.getViewComponent().addChild(texture("centipedeBodyLvl2.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop());
            }
            if(level==3){
                x.getViewComponent().clearChildren();
                x.getViewComponent().addChild(texture("centipedeBodyLvl3.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop());
            }
            CentipedeComponent comp = x.getComponent(CentipedeComponent.class);
            centipedeArray.add(x);
            comp.move(Direction.DOWN, 4);
        }
    }

    private int myRand(int lowerBound, int upperBound){//picks a random number
        boolean foundRand=false;
        int rand=(int)(Math.random() * (upperBound - lowerBound+1))+lowerBound;
        while(!foundRand){
            rand=(int)(Math.random() * (upperBound - lowerBound+1))+lowerBound;
            if(rand%5==0){
                foundRand=true;
            }
        }
        return rand;
    }

    public static void main(String[] args) {
        launch(args);
    }
}