import java.applet.*; 
import java.awt.*;

/**
 * Object.java 
 *
 * @author:
 * Assignment #:
 * 
 * Brief Program Description:
 * 
 *
 */
public class Planet implements Body
{   private double xpos, ypos,radius; //1px = 250 quadrillion units, planet radius not to scale
    private double mass; 
    private double vx, vy, fx, fy=0; //speed and forces
    private static double dt=1e10;
    private int img;
    public final double G = 6.67e-11;
    private static int multiplier=1;
    /**
     * Creates a new Body Object
     * @param doubles- position, velocity vectors, and mass
     */
    public Planet(double x, double y, double dx, double dy, double m, int i){
        xpos=x;
        ypos=y;
        mass=m;
        vx=dx;
        vy=dy;
        img=i;
    }
    public int getimg(){
        return img;
    }
    /**
     * Sets timescale that simulation runs at
     * @param double - seconds to calculate for, default 10 billion 
     * @return none
     */
    public static void setdt(double t){
        dt=t;
    }

    /**
     * Get X coordinate
     * @param none
     * @return double - x coordinate
     */
    public double getx(){
        return xpos;
    }

    /**
     * Get Y coordinate
     * @param none
     * @return double - Y coordinate
     */
    public double gety(){
        return ypos;
    }

    /**
     * Get mass of body
     * @param none
     * @return double - mass in kg
     */
    public double getMass(){
        return mass;
    }

    /**
     * Set X coordinate
     * @param double - new x coordinate
     * @return none
     */
    public void setX(double x){
        xpos=x;
    }

    /**
     * Get Y coordinate
     * @param double - new y coordinate
     * @return none
     */
    public void setY(double y){
        ypos=y;
    }

    /**
     * Increment X coordinate
     * @param double - units to increment position by
     * @return nonr
     */
    public void incX(double x){
        xpos+=x;
    }

    /**
     * Increment Y coordinate
     * @param double - units to increment position by
     * @return nonr
     */
    public void incY(double y){
        ypos+=y;
    }

    /**
     * Set Mass of body
     * @param double - new mass
     * @return none
     */
    public void setMass(double m){
        mass=m;
    }

    /**
     * Get x vector of velocity
     * @param none
     * @return double - x component of velocity
     */
    public double getvx(){
        return vx;
    }

    /**
     * Get y vector of velocity
     * @param none
     * @return double - y component of velocity
     */
    public double getvy(){
        return vy;
    }

    /**
     * Get x vector of force
     * @param none
     * @return double - x component of force
     */
    public double getfx(){
        return fx;
    }

    /**
     * Get x vector of force
     * @param none
     * @return double - x component of force
     */
    public double getfy(){
        return fy;
    }

    /**
     * Set x vector of velocity
     * @param double - x component of velocity
     * @return none
     */
    public void setvx(double v){
        vx=v;
    }

    /**
     * Set x vector of velocity
     * @param double - y component of velocity
     * @return none
     */
    public void setvy(double v){
        vy=v;
    }

    /**
     * increment x vector of velocity
     * @param double - x component of velocity to increment by
     * @return none
     */
    public void incvx(double v){
        vx+=v;
    }

    /**
     * increment y vector of velocity
     * @param double - y component of velocity to increment by
     * @return none
     */
    public void incvy(double v){
        vy+=v;
    }

    /**
     * Set x vector of force
     * @param double - x component of force
     * @return none
     */
    public void setfx(double a){
        fx=a;
    }

    /**
     * Set y vector of force
     * @param double - y component of force
     * @return none
     */
    public void setfy(double a){
        fy=a;
    }

    /**
     * increment x vector of force
     * @param double - x component of force to increment by
     * @return none
     */
    public void incfx(double a){
        fx+=a;
    }

    /**
     * increment y vector of force
     * @param double - y component of force to increment by
     * @return none
     */
    public void incfy(double a){
        fy+=a;
    }

    /**
     * Update the velocity and positions of the object by calculating acceleration and displacement
     * @param none
     * @return none
     */
    public void update() {
        vx += dt * fx / mass; //force/mass*time  (units N/kg * T or m/s/s * s) = velocity
        vy += dt * fy / mass;
        xpos += dt * vx;
        ypos += dt * vy;
    }

    /**
     * Increment Net force due to gravitational field from another body
     * @param Body - body that is accelerating this body
     * @return none
     */
    public void addForce(Body b) {
        double EPS = 3E4;  //prevents infinities or otherwise astronomically large numbers while retaining accuracy
        double dx = b.getx() - xpos; 
        double dy = b.gety() - ypos;
        double dist = Math.sqrt(dx*dx + dy*dy);//calculate distance
        double F = (G * mass * b.getMass()) / (dist*dist + EPS*EPS);//Calculate Force using newton's equation
        fx += (F * dx / dist)*multiplier;//increment net force
        fy += (F * dy / dist)*multiplier;
    }
    
    public static void setmult(int m){
        multiplier=m;
    }
}
