import java.util.*;
import java.awt.*;
import java.applet.*;  
import javax.swing.Timer;
/**
 * Driver.java  
 *
 * @author:
 * Assignment #:
 * 
 * Brief Program Description:
 * 
 *
 */
public class Main extends Applet 
{
    public final static double billion = 1000000000;          //who cares if its public if its final
    public final static double G = 6.67e-11; //oh no they might discover the universal constant of gravitation!
    private final static int DELAY = (int) (1000 / 30);

    //actual variables
    private static ArrayList<Body> bodies = new ArrayList<Body>(); //all bodies
    private static boolean pause=false;
    private static double ax,ay;
    private Timer timer;
    private static int r;
    private static double theta;
    private static double timescale=1e11;

    private static Body sol;
    private static Body earth;
    
    
    //Values to keep in mind: 
    //Most of these values are not actually used they are references
    private final static double earthMass = 5.972e24; //kg
    private final static double earthSpeed= 29780; //m/s
    private final static double solMass = 1.98892e30;
    private final static double AU = 149597870700.0/billion; //that is meters/pixel, also earth distance (1 Astronomical Unit)
    private final static int day=24*60*60; //seconds
    private final static double month=2.628e6; //seconds
    public void init(){ // creates a bunch of bodies
        double r = 1e18;        // radius of universe (yeah i know)
        for (int i = 0; i < 10; i++) {
            double x = r*exp(-1.8)*(.5-Math.random()); //places a body in a random position
            double y = r*exp(-1.8)*(.5-Math.random());
            double magv = circlev(x,y); //calculate magnitude of velocity

            double absangle = Math.atan(Math.abs(y/x)); //calculate angle of the body
            double thetav= Math.PI/2-absangle; //calculate theta
            double vx   = -1*Math.signum(y)*Math.cos(thetav)*magv; //calculate x vector
            double vy   = Math.signum(x)*Math.sin(thetav)*magv; //calculate y vector
            //Orient a random 2D circular orbit direction
            if (Math.random() <=.5) {
                vx=-vx;
                vy=-vy;
            } 

            double mass = Math.random()*solMass*10+1e20; //random mass
            bodies.add( new Body(x, y, vx, vy, mass));
        } 
        
        bodies.set(0, new Body(0,0,0,0,1e6*solMass));//Sol at center

    }
    public static double exp(double lambda) { //please ignore this horrible random math function
        return -Math.log(1 - Math.random()) / lambda;
    }
    public static void main(String[] args){
    }

    public void paint(Graphics g) {
        g.translate(250,250);//create a cartesian plane 
        if(!pause){
            for(Body b:bodies) {
                g.setColor(Color.BLUE);
                g.fillOval((int) Math.round(b.getX()*250/1e18),(int) Math.round(b.getY()*250/1e18),8,8);
            }
            //go through the Brute Force algorithm (see the function below)
            tick();
            //go through the same process again until applet is stopped
            repaint();
        }
    }
    /**
     * calculates the circular velocity of the body 
     * @param doubles - coordinates
     * @return double - velocity
     */
    public static double circlev(double x, double y) {
        double r2=Math.sqrt(x*x+y*y);
        double numerator=G*1e6*solMass;
        return Math.sqrt(numerator/r2);
    }
    
    /**
     * Calculates the distance between two points
     * @param double - 4 values,2 coordinate pairs being calculated between
     * @return double - distance between coordinates
     */
    private static double distance(double x1,double y1, double x2, double y2){
        return (  Math.sqrt( Math.pow((x2-x1),2) + Math.pow((y2-y1),2) )  );
    }

    /**
     * Calculates the distance between two bodies
     * @param Body - 2 bodies being calculated between
     * @return double - distance between bodies
     */
    private static double distance(Body star, Body planet){
        return (  Math.sqrt( (planet.getX()-star.getX())*(planet.getX()-star.getX()) + (planet.getY()-star.getY()) )  );
    }

    private static void tick(){
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).setfx(0.0); //reset forces for new iteration
            bodies.get(i).setfy(0.0);
            //2 loops = bad for processor
            for (int j = 0; j < bodies.size(); j++) {
                if (i != j){ //a body cannot accelerate itself
                    bodies.get(i).addForce(bodies.get(j)); //accelerate body i by body j
                }
            }
        }
        
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).update(); //update velocities and position
        }
    }
    /**
     * Checks if two bodies are colliding by calculating their distance and comparing to their radii
     * @param - 2 bodies being checked for collision
     * @return boolean - whether the bodies have collided or not
     */
    private static boolean collide(Body star, Body planet){
        double d=distance(star,planet);
        if(d<star.getRadius() || d<planet.getRadius()){//one body is intersecting with the other
            return true;
        }
        return false;
    }

    //Warning: Graveyard below
    //all of this code was tossed because it was too complex and didn't work effectively
    /**
     * Causes and calculates the passing of one timescale, default to one day; 30 ticks per second or one month / sec
     * @param none
     * @return none

    private static void tick(){ //this method worries me that it might require too much processing power with 2 loops
    //calculate net acceleration
    for(Planet a: planets){ //accelerated Body
    ax=0;ay=0;
    for(Body s: bodies){ //static Body performing acceleration
    ax+=accelerationX(s,a);
    ay+=accelerationY(s,a);
    }
    a.setax(ax);
    a.setay(ay);
    }
    //increment velocities and positions
    for(Planet i:planets){
    i.incvx(i.getax()*day);//1 days worth of acceleration
    i.incvy(i.getay()*day);
    i.incX((i.getvx()*day/billion));//1 days worth of moving at accelerated speed, cast to pixels from meters
    i.incY((i.getvy()*day/billion));
    }
    }*/

    
    /**
     * Calculates the magnitude of y-acceleration for a planet 
     * @param Star - body that planet is near; Planet - Body being accelerated
     * @return double - acceleration vector Y

    private static double accelerationY(Body star, Planet planet){
    if(Math.abs(star.getY()-planet.getY())<10)return 0;
    double ret= G*star.getMass() / //G M
    Math.pow( (star.getY()-planet.getY())*billion , 2); //r^2
    if(star.getY()>planet.getY())return ret;
    return -ret;
    }*/

    /**
     * Calculates the magnitude of acceleration for a planet 
     * @param Star - body that planet is near (not necessarily a star); Planet - Body being accelerated
     * @return double - acceleration vector 

    private static double acceleration(Body star, Planet planet){
    if(Math.abs(distance(star,planet))<10)return 0;
    double ret= G*star.getMass() / //G M
    Math.pow( distance(star,planet)*billion , 2); //r^2
    return ret;
    }
    private static double vectorX(Body star, Planet planet){
    final double deltaY = (star.getY() - planet.getY());
    final double deltaX = (planet.getX() - star.getX());
    theta=Math.atan2(deltaY, deltaX);
    final double ret= acceleration(star,planet)*Math.cos(theta);
    if(star.getX()>planet.getX())return ret;
    return -ret;
    }
    private static double vectorY(Body star, Planet planet){
    final double deltaY = (star.getY() - planet.getY());
    final double deltaX = (planet.getX() - star.getX());
    theta=Math.atan2(deltaY, deltaX);
    final double ret= acceleration(star,planet)*Math.sin(theta);
    if(star.getY()>planet.getY())return -ret;
    return ret;
    }*/
}
