
/**
 * Body.java  
 *
 * @author:
 * Assignment #:
 * 
 * Brief Program Description:
 * 
 *
 */
public interface Body
{
    public int getimg();
    public double getx();
    public double gety();
    public double getMass();
    
    public double getvx();
    public double getvy();
    public void setvx(double v);
    public void setvy(double v);

    public void setX(double x);
    public void setY(double y);
    public void incX(double x);
    public void incY(double y);
    public void setMass(double m);
    
    public double getfx();
    public double getfy();
    public void setfx(double f);
    public void setfy(double f);
    
    public void addForce(Body b);
    public void update();
}
