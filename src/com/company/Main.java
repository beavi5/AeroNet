package com.company;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.company.Main.*;

public class Main {
volatile static Integer count=0;
volatile static String chain="";
 static final int LEFT=2;
 static final int RIGHT=1;
 static final int TOP=4;
 static final int BOTTOM=3;
 static int SIZE=4;

    public static boolean[][] wallsVertical;
    public static boolean[][] wallsHorizontal;
  public static int[][] holes;

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner=new Scanner(System.in);
        int[] setupArray = Arrays.stream(scanner.nextLine().split(" ")).mapToInt((x)-> Integer.parseInt(x)).toArray();
  //    int[]  setupArray= new int[]{4, 2, 2, 2, 2, 1, 4, 1, 1, 4};

        SIZE=setupArray[0];
        holes = new int[SIZE][SIZE];

        wallsVertical = new boolean[SIZE][SIZE];
         wallsHorizontal = new boolean[SIZE][SIZE];
        CopyOnWriteArrayList<Ball> balls = new CopyOnWriteArrayList<>();

        for (int i = 0; i < setupArray[1] ; i++) {
//2 2 2 2 2 2 2 2 2 2
            //4 2 2 2 2 1 4 1 1 4


            balls.add(new Ball(setupArray[2*i+2]-1,setupArray[2*i+2+1]-1,i+1));
            holes[setupArray[2+setupArray[1]*2+2*i]-1] [setupArray[3+setupArray[1]*2+2*i]-1]=i+1;

        }

        for (int i = 2+4*setupArray[1]; i < setupArray.length - 4 ; i=i+4) {
                if (setupArray[i]==setupArray[i+2])
                {if (setupArray[i+1]<setupArray[i+3])
                    wallsVertical[setupArray[i]][setupArray[i+1]]=true;
                else  wallsVertical[setupArray[i+2]][setupArray[i+3]]=true;}

                else
                    {if (setupArray[i+1]<setupArray[i+3])
                    wallsHorizontal[setupArray[i]][setupArray[i+1]]=true;
                else  wallsHorizontal[setupArray[i+2]][setupArray[i+3]]=true;}

        }



       // holes [1][1]=1;
        //holes [3][0]=1;


      //  wallsVertical[0][2]=true;
        //wallsHorizontal[1][0]=true;

        //balls.add(new Ball(0,0));
       // balls.add(new Ball(2,3));

        Thread thread1 = new MyThread(count, copyBallsArr(balls),RIGHT,"");
        Thread thread2 = new MyThread(count,copyBallsArr(balls),LEFT,"");
        Thread thread3 = new MyThread(count, copyBallsArr(balls),BOTTOM,"");
        Thread thread4 = new MyThread(count,copyBallsArr(balls),TOP,"");
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
       // Thread.sleep(4000);
        System.out.println("Минимальное количество ходов: "+count);
        System.out.println("Последовательность ходов: "+chain);

    }


static public CopyOnWriteArrayList<Ball> copyBallsArr (CopyOnWriteArrayList<Ball> balls){
    CopyOnWriteArrayList<Ball> ballz = new CopyOnWriteArrayList<Ball>();
    for (Ball ball :balls) {
        ballz.add(new Ball(ball.getX(),ball.getY(),ball.getNumber()));
    }

    return ballz;
}
}

class MyThread extends Thread
{
    Integer count;
    CopyOnWriteArrayList<Ball> balls = new CopyOnWriteArrayList<Ball>();
    String chain;


    public MyThread(Integer count, CopyOnWriteArrayList<Ball> balls,   int to, String chain) {
        this.count = count;
        this.chain = chain;
        this.balls = balls;
        this.to = to;
    }

    int to;
    @Override
    public void run() {
        super.run();

        synchronized (Main.count) {
            if (Main.count != 0 && count>Main.count) {return;} //условие выхода: счетчик ходов больше минимального решения
     //   }



                 //условие выхода: количество шаров = 0
   //     synchronized (Main.count) {
            if (balls.size()<=0) {
            if (Main.count == 0) { Main.count=count;
              Main.chain=chain;
            //System.out.println(chain);
            }
            else if (count<Main.count ) {Main.count=count;
               // System.out.println(count);
                Main.chain=chain;

            }
                return;}
        }

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        switch (to){
            case RIGHT:   //смещение слева направо
            {
                chain+="RIGHT_";
                               for (Ball ball : balls) {

                                   outer:     for (int i = ball.getY(); i < SIZE; i++) {
                        //  if (holes[ball.getX()][i]) {ball.setY(i); break;}
                        if (Main.wallsVertical[ball.getX()]
                                [i]) {
                            ball.setY(i);

                        if (Main.holes[ball.getX()][i]!=0) {
                           balls.remove(ball);
                        //    System.out.println("AAAAAAAAAAA!");

                        }
                            break outer;
                        }
                        else if (Main.holes[ball.getX()][i]!=0) {
                            balls.remove(ball);
                         //   System.out.println("AAAAAAAAAAA!");
                            break outer;
                        }

                        if (i == SIZE-1) ball.setY(SIZE-1);
                    }


                }

                Thread thread2 = new MyThread(count+1,copyBallsArr(balls),LEFT,chain);
                Thread thread3 = new MyThread(count+1, copyBallsArr(balls),BOTTOM,chain);
                Thread thread4 = new MyThread(count+1,copyBallsArr(balls),TOP,chain);
                thread2.start();
                thread3.start();
                thread4.start();
                try {
                    thread2.join();

                thread3.join();
                thread4.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            case LEFT: //смещение справа налево
            {chain+="LEFT_";
                for (Ball ball : balls) {
                    for (int i = ball.getY(); i >=0 ; i--) {

                        if (Main.wallsVertical[ball.getX()][i]) {
                            ball.setY(i+1);


                        if (Main.holes[ball.getX()][i]!=0) {
                            balls.remove(ball);
                          //  System.out.println("AAAAAAAAAAA!");
                        }
                            break;}
                        else if (Main.holes[ball.getX()][i]!=0) {
                            balls.remove(ball);
                         //   System.out.println("AAAAAAAAAAA!");
                            break;
                        }


                        if (i==0) ball.setY(0);
                    }



                }
                Thread thread1 = new MyThread(count+1, copyBallsArr(balls),RIGHT,chain);
                Thread thread3 = new MyThread(count+1, copyBallsArr(balls),BOTTOM,chain);
                Thread thread4 = new MyThread(count+1,copyBallsArr(balls),TOP,chain);
                thread1.start();
                thread3.start();
                thread4.start();
                try {
                    thread1.join();

                    thread3.join();
                    thread4.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;}
            case BOTTOM: //смещение сверху вниз
            {
                chain+="BOT_";
                for (Ball ball : balls) {
                    for (int i = ball.getX(); i < SIZE ; i++) {

                        if (Main.wallsHorizontal[i][ball.getY()]) {
                            ball.setX(i);
                        if (Main.holes[i][ball.getY()]!=0) {
                            balls.remove(ball);
                           // System.out.println("AAAAAAAAAAA!");
                            break;
                        }
                            break;}
                        else if (Main.holes[i][ball.getY()]!=0) {
                            balls.remove(ball);
                          //  System.out.println("AAAAAAAAAAA!");
                            break;
                        }


                        if (i==SIZE-1) ball.setX(SIZE-1);
                    }



                }
                Thread thread1 = new MyThread(count+1, copyBallsArr(balls),RIGHT,chain);
                Thread thread2 = new MyThread(count+1,copyBallsArr(balls),LEFT,chain);
                Thread thread4 = new MyThread(count+1,copyBallsArr(balls),TOP,chain);
                thread1.start();
                thread2.start();
                thread4.start();
                try {
                    thread2.join();

                    thread2.join();
                    thread4.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;}
            case TOP: //смещение снизу вверх
            {
                chain+="TOP_";
                for (Ball ball : balls) {
                    for (int i = ball.getX(); i >= 0; i--) {

                        if (Main.wallsHorizontal[i][ball.getY()]) {
                            ball.setX(i+1);
                            if (Main.holes[i][ball.getY()]!=0) {
                                balls.remove(ball);
                                // System.out.println("AAAAAAAAAAA!");
                                break;
                            }
                            break;}
                        else if (Main.holes[i][ball.getY()]!=0) {
                            balls.remove(ball);
                            //  System.out.println("AAAAAAAAAAA!");
                            break;
                        }

                        if (i == 0) ball.setX(0);
                    }


                }
                for (Ball ball : balls) {
                //    System.out.println(Thread.currentThread().getName()+"--- "+ball.getX()+" "+ ball.getY() +"   smesh "+to);
                }

             Thread    thread1 = new MyThread(count+1, copyBallsArr(balls), RIGHT,chain);
                Thread thread2 = new MyThread(count+1, copyBallsArr(balls), LEFT,chain);
                Thread thread3 = new MyThread(count+1, copyBallsArr(balls), BOTTOM,chain);
                thread1.start();
                thread2.start();
                thread3.start();
                try {
                    thread2.join();

                    thread3.join();
                    thread1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }


        for (Ball ball : balls) {
          //  System.out.println(Thread.currentThread().getName()+"--- "+ball.getX()+" "+ ball.getY() +"   smesh "+to);
        }

//        try {
//            Thread.sleep(6000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}


class Ball {
    int x,y,number;

    public int getNumber() {
        return number;
    }

    public Ball(int x, int y, int number) {
        this.x = x;

        this.y = y;
        this.number = number;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {

        return x;
    }

    public int getY() {
        return y;
    }

}

