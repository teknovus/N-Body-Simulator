
/**
 * Star.java  
 *
 * @author:
 * Assignment #:
 * 
 * Brief Program Description:
 * 
 *
 */
public class Star implements Body
{
    private double xpos, ypos,radius; //1px = 250 quadrillion units, planet radius not to scale
    private double mass; 
    private int img;
    private double dt=1e10;

    public final double G = 6.67e-11;
    /**
     * Creates a new Body Object
     * @param doubles- position, velocity vectors, and mass
     */
    public Star(double x, double y, double m, int i){
        xpos=x;
        ypos=y;
        mass=m;
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
    public void setdt(double t){
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
    
    //methods stars do not implement
    public void setfx(double f){}
    public void setfy(double f){}
    
    public void addForce(Body b){}
    public void update(){}
    public double getfx(){
        return 0;
    }

    public double getfy(){
        return 0;
    }
    public double getvx(){
        return 0;
    }
    public double getvy(){
        return 0;
    }
    public void setvx(double v){}
    public void setvy(double v){}
}
