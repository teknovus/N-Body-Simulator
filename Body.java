
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
public class Body
{   private double xpos, ypos,radius; //1px = 1 billion meters, planet radius not to scale
    private double mass; 
    private double vx, vy, fx, fy=0; //speed and forces
    private double dt=1e10;
    public final double G = 6.67e-11;
    public Body(double x, double y, double dx, double dy, double m){
        xpos=x;
        ypos=y;
        mass=m;
        vx=dx;
        vy=dy;
    }
    
    public void setdt(double t){
        dt=t;
    }
    
    public double getX(){
        return xpos;
    }

    public double getY(){
        return ypos;
    }

    public double getRadius(){
        return radius;
    }

    public double getMass(){
        return mass;
    }

    public void setX(double x){
        xpos=x;
    }

    public void setY(double y){
        ypos=y;
    }

    public void incX(double x){
        xpos+=x;
    }

    public void incY(double y){
        ypos+=y;
    }

    public void setRadius(double r){
        radius=r;
    }

    public void setMass(double m){
        mass=m;
    }

    public double getvx(){
        return vx;
    }

    public double getvy(){
        return vy;
    }

    public double getfx(){
        return fx;
    }

    public double getfy(){
        return fy;
    }

    public void setvx(double v){
        vx=v;
    }

    public void setvy(double v){
        vy=v;
    }

    public void incvx(double v){
        vx+=v;
    }

    public void incvy(double v){
        vy+=v;
    }

    public void setfx(double a){
        fx=a;
    }

    public void setfy(double a){
        fy=a;
    }

    public void incfx(double a){
        fx+=a;
    }

    public void incfy(double a){
        fy+=a;
    }

    public void update() {
        vx += dt * fx / mass;
        vy += dt * fy / mass;
        xpos += dt * vx;
        ypos += dt * vy;
    }

    public void addForce(Body b) {
        double EPS = 3E4;     
        double dx = b.getX() - xpos;
        double dy = b.getY() - ypos;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double F = (G * this.mass * b.mass) / (dist*dist + EPS*EPS);
        fx += F * dx / dist;
        fy += F * dy / dist;
    }
}
