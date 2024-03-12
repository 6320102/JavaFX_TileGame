//宇宙の画像元 https://pictkan.com/photo/nature/universe/%E6%BA%80%E5%A4%A9%E3%81%AE%E6%98%9F%E7%A9%BA-%E5%AE%87%E5%AE%99-%E9%8A%80%E6%B2%B3/noW
//その他の画像元 https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.ac-illust.com%2Fmain%2Fsearch_result.php%3Fword%3D%25E3%2582%25B7%25E3%2583%25A5%25E3%2583%25BC%25E3%2583%2586%25E3%2582%25A3%25E3%2583%25B3%25E3%2582%25B0%25E3%2582%25B2%25E3%2583%25BC%25E3%2583%25A0&psig=AOvVaw3XVtJcdqfTmjesOFVUYQ5_&ust=1642749660781000&source=images&cd=vfe&ved=0CAwQjhxqFwoTCKC2hMXlv_UCFQAAAAAdAAAAABAs

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Game extends Application{
    GraphicsContext gc;
    Map m=new Map();
    int craftX=11;
    int craftY=20;
    int shootMotion;
    int time;
    int flash=0; //0のとき点滅しない
    int playerLevel=1;
    int HP=5;
    List<int[]> bullets=new ArrayList<>();
    List<int[]> enemyBullet=new ArrayList<>();
    RandomStage rs=new RandomStage();
    int gameset=0; //通常0、ゲームオーバーで1、クリアーで2

    @Override
    public void start(Stage st) throws Exception{
        Group root = new Group();
        Canvas canvas = new Canvas(400,500);
        gc=canvas.getGraphicsContext2D();
        draw();
    
        root.getChildren().add(canvas);

        Scene scene = new Scene(root,400,500,Color.WHITE);
        st.setTitle("Game");
        st.setScene(scene);
        st.show();
        
        AnimationTimer timer=new AnimationTimer(){
            @Override
            public void handle(long t){
                time++;
                shootMotion=time%2;
                shoot();
                flashCraft();
                enemy1Shoot();
                enemy2Shoot();
                enemy3Shoot();
                enemyBulletMove();
                draw();
            }
        };
        timer.start();

        scene.setOnKeyPressed(this::keyPressed);
    }

    public static void main(String[] a){
        launch(a);
    }

    void setMap(){
        for(int i=0; i<rs.item.size(); i++){
            m.setItem(rs.item.get(i)[0],rs.item.get(i)[1]);
        }
        for(int i=0; i<rs.enemy.size(); i++){
            int x=rs.enemy.get(i)[0];
            int y=rs.enemy.get(i)[1];
            switch(rs.enemy.get(i)[3]){
                case '1','2':
                    m.setEnemy12(x,y);
                    break;
                case '3':
                    m.setEnemy3(x,y);
                    break;
            }
        }
        if(flash==0 && gameset==0){
            damage();
        }
        if(HP>0){
            m.setCraft(craftX,craftY);
        }
    }

    public void draw(){
        int flashtiming=time%10;
        boolean[] drawRazer=new boolean[m.xmax];
        for(int i=0; i<m.xmax; i++){
            drawRazer[i]=true;
        }
        
        setMap();
        gc.clearRect(0,0,400,500);
        gc.drawImage(new Image("space.jpg"),0,0,400,500);
        if(gameset==1 && flash==0){
            gc.setFill(Color.YELLOW);
            gc.fillText("Game Over...",150,200);
            return;
        }else if(gameset==2){
            gc.setFill(Color.YELLOW);
            gc.fillText("Clear!",170,200);
            return;
        }

        double xSize=400/(double)m.xmax;
        double ySize=400/(double)m.ymax;
        double x=0,y=0;

        //敵1,2の描写
        for(int i=0; i<rs.enemy.size(); i++){
            int e[]=rs.enemy.get(i);
            x=xSize*(e[0]-2);
            y=ySize*(e[1]+0.5);
            if(e[3]=='1'){
                gc.drawImage(new Image("enemy11.png"),x,y,xSize*3,ySize*3);
            }else if(e[3]=='2'){
                gc.drawImage(new Image("enemy21.png"),x,y,xSize*3,ySize*3);
            }
        }

        for(int j=1; j<=m.ymax; j++){
            for(int i=1; i<=m.xmax; i++){
                x=xSize*(i-1);
                y=ySize*(j+1.5);

                switch(m.map[j][i]){
                    case 'c': //弾
                        gc.drawImage(new Image("bullet1.png"),x-1,y,xSize,ySize);
                        break;
                    case 'I': //アイテム
                        gc.drawImage(new Image("item1.png"),x,y,xSize,ySize);
                        break;
                    case 'e': //敵の弾
                        gc.drawImage(new Image("enemyBullet1.png"),x+3,y,xSize/1.5,ySize*1.5);
                        break;
                    case 'l':
                        if(drawRazer[i]){
                            gc.drawImage(new Image("lazer1.png"),x,y,xSize,400);
                            drawRazer[i]=false;
                        }
                        break;
                    case 'E': //敵3
                        for(int n=0; n<rs.enemy.size(); n++){
                            if(rs.enemy.get(n)[0]==i && rs.enemy.get(n)[1]==j && rs.enemy.get(n)[3]=='3'){
                                gc.drawImage(new Image("enemy31.png"),x,y,xSize,ySize);
                            }
                        }
                        break;
                }
            }
        }

        x=xSize*(craftX-2);
        y=ySize*(craftY+0.5);
        //操作する機体の描画
        if(gameset!=1 && (flash==0 || flashtiming==0 || flashtiming==1 || flashtiming==2 || flashtiming==3 || flashtiming==4)){
            gc.drawImage(new Image("craft1.png"),x,y,xSize*3,ySize*3);
        }else if(gameset==1 && flash!=0){
            x=xSize*(craftX-3);
            y=ySize*(craftY-0.5);
            gc.drawImage(new Image("explosion1.png"),x,y,xSize*5,ySize*5);
        }

        //体力の表示
        gc.setStroke(Color.BROWN);
        gc.setLineWidth(2.0);
        gc.strokeRect(50,470,300,20);
        switch(HP){
            case 4,5:
                gc.setFill(Color.LIGHTGREEN);
                break;
            case 2,3:
                gc.setFill(Color.YELLOW);
                break;
            case 1:
                gc.setFill(Color.RED);
                break;
        }
        gc.fillRect(50,470,60*HP,20);
    }

    public void keyPressed(KeyEvent e){
        if(HP==0){
            return;
        }
        KeyCode key = e.getCode();
        switch (key){
            case LEFT:
                moveCraft(0);
                break;
            case RIGHT:
                moveCraft(1);
                break;
            case UP:
                moveCraft(2);
                break;
            case DOWN:
                moveCraft(3);
                break;
            default: return;
        }
        draw();
    }

    public void moveCraft(int dir){
        int dx=0,dy=0;
        switch(dir){
            case 0:
                dx=-1;
                break;
            case 1:
                dx=1;
                break;
            case 2:
                dy=-1;
                break;
            case 3:
                dy=1;
                break;
        }

        if(craftX+dx>1 && craftX+dx<m.xmax && craftY+dy>1 && craftY+dy<=m.ymax){
            m.map[craftY-1][craftX]=' ';
            for(int i=-1; i<=1; i++){
                m.map[craftY][craftX+i]=' ';
            }
            craftX+=dx;
            craftY+=dy;
            gotItem();
            m.setCraft(craftX,craftY);
        }
    }

    void shoot(){
        int timing=1;
        switch(playerLevel){
            case 1:
                timing=time%60;
                break;
            case 2:
                timing=time%40;
                break;
            case 3:
                timing=time%10;
        }

        if(HP>0){
            //弾丸生成
            switch(timing){
                case 0:
                    int[] b={craftX,craftY-2};
                    bullets.add(b);
            }
        }

        //弾丸の移動
        switch(shootMotion){
            case 0:
                for(int i=0; i<bullets.size(); i++){
                    int bulletX=bullets.get(i)[0];
                    int bulletY=bullets.get(i)[1];
                    char c=m.map[bulletY][bulletX];

                    if(c==' ' || c=='I' || c=='e' || c=='l'){
                        m.map[bulletY+1][bulletX]=' ';
                        m.map[bulletY][bulletX]='c';
                        int[] b={bulletX,bulletY-1};
                        bullets.set(i,b);
                    }else{
                        if(m.map[bulletY][bulletX]=='E'){
                            attack(bulletX,bulletY);
                            if(rs.enemy.size()==0){
                                gameset=2;
                                return;
                            }
                        }
                        m.map[bulletY+1][bulletX]=' ';
                        bullets.remove(i);
                    }
                }
                break;
        }
    }

    void enemy1Shoot(){
        int shootTiming=time%80;
        for(int i=0; i<rs.enemy.size(); i++){
            if(rs.enemy.get(i)[3]=='1'){
                int[] b=new int[2]; //{x,y,敵の種類}
                b[0]=rs.enemy.get(i)[0];
                b[1]=rs.enemy.get(i)[1]+2;
                //弾丸の生成
                switch(shootTiming){
                    case 42:
                        enemyBullet.add(b);
                        m.map[b[1]][b[0]]='e';
                        break;
                }
            }
        }
    }

    void enemy2Shoot(){
        int shootTiming=time%50;
        for(int i=0; i<rs.enemy.size(); i++){
            if(rs.enemy.get(i)[3]=='2'){
                int[] b=new int[2]; //{x,y,敵の種類}
                b[0]=rs.enemy.get(i)[0];
                b[1]=rs.enemy.get(i)[1]+2;
                //弾丸の生成
                switch(shootTiming){
                    case 22:
                        enemyBullet.add(b);
                        m.map[b[1]][b[0]]='e';
                        break;
                }
            }
        }
    }

    void enemy3Shoot(){
        int shootTiming=time%350;
        for(int i=0; i<rs.enemy.size(); i++){
            if(rs.enemy.get(i)[3]=='3'){
                int[] b={rs.enemy.get(i)[0],rs.enemy.get(i)[1]};
                if(shootTiming==0){
                    //ビーム削除
                    while(b[1]<m.ymax){
                        m.map[b[1]][b[0]]=' ';
                        b[1]++;
                    }
                }else if(264<=shootTiming && shootTiming<350){
                    //ビーム
                    while(b[1]<m.ymax){
                        m.map[b[1]][b[0]]='l';
                        b[1]++;
                    }
                }
            }
        }
    }

    void enemyBulletMove(){
        for(int i=0; i<enemyBullet.size(); i++){
            int b[]=enemyBullet.get(i);
            //弾の移動
            switch(shootMotion){
                case 1:
                    if(b[1]>m.ymax){
                        break;
                    }
                    if(m.map[b[1]][b[0]]=='C' && flash==0){
                        damage();
                        enemyBullet.remove(i);
                        break;
                    }else if(m.map[b[1]+1][b[0]]=='C' && flash==0){
                        m.map[b[1]+1][b[0]]='e';
                        damage();
                        m.map[b[1]][b[0]]=' ';
                        enemyBullet.remove(i);
                        break;
                    }else if(m.map[b[1]+1][b[0]]!='B'){
                        //進む処理
                        m.map[b[1]][b[0]]=' ';
                        b[1]++;
                        enemyBullet.set(i,b);
                        m.map[b[1]][b[0]]='e';
                    }else{
                        m.map[b[1]][b[0]]=' ';
                        enemyBullet.remove(i);
                    }
                    break;
            }
        }
    }

    void flashCraft(){
        int flashtiming=time%10;
        if(flash!=0){
            switch(flashtiming){
                case 4:
                    flash++;
                    break;
            }
            if(flash==10){
                flash=0;
            }
        }
    }

    void gotItem(){
        for(int i=0; i<rs.item.size();i++){
            int itemX=rs.item.get(i)[0];
            int itemY=rs.item.get(i)[1];
            boolean case1=(craftX==itemX)&&(craftY-1==itemY);
            boolean case2=(craftX+1==itemX)&&(craftY==itemY);
            boolean case3=(craftX-1==itemX)&&(craftY==itemY);
            boolean case4=(craftX==itemX)&&(craftY==itemY);
    
            if(case1 || case2 || case3 || case4){
                if(playerLevel<3){
                    playerLevel++;
                }
                rs.item.remove(i);
                m.map[itemY][itemX]=' ';
            }
        }
    }

    void damage(){
        boolean case1=(m.map[craftY][craftX-1]=='E');
        boolean case2=(m.map[craftY][craftX+1]=='E');
        boolean case3=(m.map[craftY-1][craftX]=='E');
        boolean case4=(m.map[craftY][craftX]=='E');

        boolean case5=(m.map[craftY][craftX-1]=='e');
        boolean case6=(m.map[craftY][craftX+1]=='e');
        boolean case7=(m.map[craftY-1][craftX]=='e');

        boolean case8=(m.map[craftY][craftX-1]=='l');
        boolean case9=(m.map[craftY][craftX+1]=='l');
        boolean case10=(m.map[craftY-1][craftX]=='l');
    
        if(case1 || case2 || case3 || case4 || case5 || case6 || case7 || case8 || case9 || case10){
            if(HP>1){
                HP--;
                flash=1;
            }else{
                HP=0;
                flash=6;
                gameset=1;
                m.map[craftY-1][craftX]=' ';
                for(int i=-1; i<=1; i++){
                    m.map[craftY+i][craftX]=' ';
                }
            }
        }
    }

    void attack(int x,int y){
        for(int i=0; i<rs.enemy.size(); i++){
            int doesRemove=0;
            int[] e=rs.enemy.get(i);
            switch(e[3]){
                case '1','2':
                    boolean case1=(x==e[0]-1 && y==e[1]);
                    boolean case2=(x==e[0]+1 && y==e[1]);
                    boolean case3=(x==e[0] && y==e[1]+1);
                    if(case1 || case2 || case3){
                        e[2]--;
                        if(e[2]==0){
                            m.map[e[1]][e[0]-1]=' ';
                            m.map[e[1]][e[0]]=' ';
                            m.map[e[1]][e[0]+1]=' ';
                            m.map[e[1]+1][e[0]]=' ';
                            doesRemove=1;
                        }
                    }else{
                        break;
                    }
                    doesRemove++;
                    break;
                case '3':
                    if(x==e[0] && y==e[1]){
                        e[2]--;
                        if(e[2]==0){
                            m.map[y][x]=' ';
                            //ビーム削除
                            int[] b={rs.enemy.get(i)[0],rs.enemy.get(i)[1]};
                            while(b[1]<m.ymax){
                                m.map[b[1]][b[0]]=' ';
                                b[1]++;
                            }
                            doesRemove=1;
                        }
                    }else{
                        break;
                    }
                    doesRemove++;
                    break;
            }
            if(doesRemove==2){
                rs.enemy.remove(i);
                return;
            }else if(doesRemove==1){
                rs.enemy.set(i,e);
                return;
            }
        }
    }
}

class Map{
    char[][] map;
    int xmax=20,ymax=20;

    Map(){
        map=new char[xmax+2][ymax+2];
        for(int j=0; j<map.length; j++){
            for(int i=0; i<map[j].length; i++){
                if(i==0 || j==0 || i==xmax+1 || j==ymax+1){
                    map[j][i]='B';
                }else{
                    map[j][i]=' ';
                }
            }
        }
    }

    void setCraft(int x,int y){
        map[y][x]='C';
        map[y][x+1]='C';
        map[y][x-1]='C';
        map[y-1][x]='C';
    }

    void setItem(int x,int y){
        map[y][x]='I';
    }

    void setEnemy12(int x,int y){
        map[y][x]='E';
        map[y][x-1]='E';
        map[y][x+1]='E';
        map[y+1][x]='E';
    }


    void setEnemy3(int x,int y){
        map[y][x]='E';
    }
}

class RandomStage{
    List<int[]> item=new ArrayList<>();
    //{x,y,体力,種類}
    List<int[]> enemy=new ArrayList<>();

    RandomStage(){
        item();
        enemy();
    }

    void item(){
        double r=Math.random();
        if(0<=r && r<0.5){
            int[] i1={11,17};
            item.add(i1);
        }else{
            int[] i1={10,15};
            item.add(i1);
            int[] i2={12,18};
            item.add(i2);
        }
    }
    
    void enemy(){
        double r=Math.random();
        if(0<=r && r<0.5){
            int[] e1={3,1,10,'1'};
            enemy.add(e1);
            int[] e2={18,1,10,'1'};
            enemy.add(e2);
            int[] e3={8,3,20,'2'};
            enemy.add(e3);
            int[] e4={13,3,20,'2'};
            enemy.add(e4);
            int[] e5={5,4,5,'3'};
            enemy.add(e5);
            int[] e6={9,4,5,'3'};
            enemy.add(e6);
            int[] e7={12,4,5,'3'};
            enemy.add(e7);
            int[] e8={16,4,5,'3'};
            enemy.add(e8);
        }else{
            int[] e1={2,1,10,'1'};
            enemy.add(e1);
            int[] e2={19,1,10,'1'};
            enemy.add(e2);
            int[] e3={10,2,20,'2'};
            enemy.add(e3);
            int[] e4={4,3,20,'2'};
            enemy.add(e4);
            int[] e5={17,3,20,'2'};
            enemy.add(e5);
            int[] e6={6,5,10,'1'};
            enemy.add(e6);
            int[] e7={9,5,5,'3'};
            enemy.add(e7);
            int[] e8={12,5,5,'3'};
            enemy.add(e8);
            int[] e9={15,5,10,'1'};
            enemy.add(e9);
            int[] e10={3,6,5,'3'};
            enemy.add(e10);
            int[] e11={18,6,5,'3'};
            enemy.add(e11);
            int[] e12={8,7,5,'3'};
            enemy.add(e12);
            int[] e13={13,7,5,'3'};
            enemy.add(e13);
        }
    }
}