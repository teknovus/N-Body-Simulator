import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.applet.*;  
/**
 * Main.java  
 *
 * @author: Josh Hu
 * Version: 2.0
 * 
 * Brief Program Description:
 * 
 * This simulation visually shows planets in orbit of a sun with (reasonably) accurate physics and math
 * By reasonably accurate, all values are essentially 1 billion x of what they should be in reality to maintain accuracy of the smaller values
 * 
 * It utilizes a totally brute force approach towards calculations by manually calculating forces for every permutation of bodies
 * This makes it a little slow 
 * Adding more planets makes it exponentially slower, n^2+n iterations (https://en.wikipedia.org/wiki/N-body_simulation#Calculation_optimizations)
 * 
 * No Enhanced For loops are used in this simulation due to the fact that arraylist bodies can be changed and
 * enhanced for loops have issues when the list they iterate through is changed mid loop
 *
 * Unsolved Issues:
 * 
 * When adding bodies via clicking, sometimes the program does not respond, I think it may because it won't accept the mouseevent input if its 
 * in the middle of calculating or looping or something
 * 
 * In rare cases, when restart is called, it has one less body than it should, location checking may be deleting it? (unlikely? Only possibility tho?)
 * 
 * The simulator does not account for the fact that gravitational waves are limited by the speed of light 
 * 
 * Pending ideas:
 * Utilization of Newton's 3rd law could cut iterations in half? (i.e. addforces calls the other object and gives it the same force)
 *      Unsure on how to check if a body has already had the 3rd law applied (for loop restrictions? int j=i; ?)
 * Implement accurate collision bounces by calculating angle of bounce location with trig (or really any collisions)
 *      Unsure on how to calculate exact collision location (average coordinates?) Also unsure on how to fix collision detection (dist equation is moot)
 *      
 * Additional Notes:
 * Star and Planet rightfully should be renamed static (or some variant thereof due to reserved) and dynamic respectively as their current titles are 
 * no longer completely accurate representations of them, however for all intents and purposes they are just fine named as they are
 * Plus I don't want to go and replace every iteration of star and planet
 */
public class Main extends Applet implements MouseListener
{
    public final static double invScale=1e18/250;//this one exists because multiplication is faster than division and i'm concerned about runtime
    public final static double scale=250/1e18;  //who cares if its public if its final
    public final static double G = 6.67e-11;   //oh no they might discover the universal constant of gravitation!

    //actual variables
    private static ArrayList<Body> bodies = new ArrayList<Body>(); //all bodies
    private static boolean pause=false; //self explanatory
    private static boolean collision=false; //currently not implemeneted
    private static boolean star=true; //Is the user adding a star or planet
    private static boolean dynamic=false; //are stars dynamic or static 
    private static int centerx = 500; //default origin with respect to 0,0
    private static int centery = 500; 
    private static int N=2; //default bodies: one sun five planets

    private Image img0,img1,img2,img3,img4,img5,img6,bg;
    private final static double solMass = 1.98892e30; //actual mass of our sun Sol, used as math reference point

    private TextField t1;
    private Label l1;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private Button b6;
    /**
     * Initialize bodies, images, buttons, universe
     */
    public void init(){ 
        addBodies(N);// creates a bunch of bodies

        bodies.set(0, new Star(0,0,1e6*solMass,0));//Sol at center, mass is solar mass x 1 million due to scaling
        //load images
        img0=getImage(getCodeBase(), "sun.png");
        img1=getImage(getCodeBase(), "earth.png");
        img2=getImage(getCodeBase(), "barren.png");
        img3=getImage(getCodeBase(), "dust.png");
        img4=getImage(getCodeBase(), "tundra.png");
        img5=getImage(getCodeBase(), "fe.png");
        img6=getImage(getCodeBase(), "neptune.png");

        bg=getImage(getCodeBase(), "bg.jpg");

        addMouseListener(this);
        //initialize buttons, fields, and labels
        t1=new TextField("5",5); //default value
        b1=new Button("Pause");
        b2=new Button("Restart");
        b3=new Button("Add");
        b4=new Button("REMOVE STARS");
        b5=new Button("Add Star");
        b6=new Button("Static");
        l1=new Label("Number of bodies:");

        ButtonListener myButtonListener = new ButtonListener();
        b1.addActionListener(myButtonListener);
        b2.addActionListener(myButtonListener);
        b3.addActionListener(myButtonListener);
        b4.addActionListener(myButtonListener);
        b5.addActionListener(myButtonListener);
        b6.addActionListener(myButtonListener);
        add(l1);
        add(t1);
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);
    }

    /**
     * Animates the motion of the bodies
     */
    public void paint(Graphics g) {
        g.drawImage(bg,0,0,this); //paint background
        g.translate(centerx,centery);//create a cartesian plane with origin at 500 500

        for(int i=0;i<bodies.size();i++) {
            int x=(int) Math.round(bodies.get(i).getx()*scale);//fetch and scale paint positions
            int y=(int) Math.round(bodies.get(i).gety()*scale);

            switch(bodies.get(i).getimg()){ //call image number
                case 0:g.drawImage(img0,x-24,y-24,48,48,this);break; //paint sun
                case 1:g.drawImage(img1,x-12,y-12,24,24,this);break; //paint planetary bodies
                case 2:g.drawImage(img2,x-12,y-12,24,24,this);break;
                case 3:g.drawImage(img3,x-12,y-12,24,24,this);break;
                case 4:g.drawImage(img4,x-12,y-12,24,24,this);break;
                case 5:g.drawImage(img5,x-12,y-12,24,24,this);break;
                case 6:g.drawImage(img6,x-12,y-12,24,24,this);break;
            }
            //Collect Garbage 
            if(Math.abs(x)>600 || Math.abs(y)>600){ //Is position too far off screen?
                bodies.remove(i);
                i--; //set back iterator due to arraylist change
            }
        }
        if(!pause){//run only if sim is not paused
            //pass one tick
            tick();
        }
        repaint();
    }

    /**
     * Adds n bodies to the simulation and arraylist, randomly assigning position and mass, mathematically assigning velocity
     * @param int - number of bodies to be added
     * @return none
     */
    public static void addBodies(int n){
        for (int i = 0; i < n; i++) {
            //          scale   rand num          rand sign 
            double x = 1e18*(Math.random())*Math.signum(.5-Math.random()); //places a body in a random position within the confines of the
            double y = 1e18*(Math.random())*Math.signum(.5-Math.random()); //size of the universe and also at least 5 px from the sun
            double v = circlev(x,y); //calculate magnitude of velocity

            double absangle = Math.atan(Math.abs(y/x)); //calculate angle of the body
            double theta= Math.PI/2-absangle; //calculate theta

            //  x is neg because of how trig and origin work
            //            leading sign  vector component  magnitude
            double vx   = -1*Math.signum(y)*Math.cos(theta)*v; //calculate x vector, signum is to fetch sign, trig to get component of v
            double vy   = Math.signum(x)*Math.sin(theta)*v; //calculate y vector, x vector sign is inverted to work with maths
            //Pick Clockwise or counterclockwise randomly
            if (Math.random() <=.5) {
                vx=-vx;
                vy=-vy;
            } 

            double mass = Math.random()*solMass+1e20; //random mass within acceptable bounds (some random percentage of solar mass+1e20 minimum)
            //mass is not relevant to stable orbits for two reasons
            //Centripetal Acceleration is independent of mass in this situation
            //The sun is so exceedingly massive that interplanetary forces are *usually* negligible 
            //Footnote: Simulations with many bodies can and will destroy each others' orbits throwing bodies off the screen

            int img=(int)(Math.random()*6)+1;// random image number, 1-6 inclusive
            bodies.add( new Planet(x, y, vx, vy, mass,img)); //add body to arraylist 
        } 
    }

    /** 
     * calculates the magnitude of circular velocity of the body (speed, unvectorized)
     * this method was not given a tester because it is an application of known physics equations
     * @param doubles - coordinates
     * @return double - velocity
     */
    public static double circlev(double x, double y) { 
        //Equations derived from Newton's Gravitation and Centripetal Force (Gmm/r2 and mv2/r respectively) to form the equation sqrt(GM/r)
        //to calculate velocity magnitude of a mass in orbit of body with mass M
        double r2=Math.sqrt(x*x+y*y); // 1/r  (where r is the distance equation)
        double numerator=G*1e6*solMass; //G*M (where main sun is a body with mass solMass*1000000)
        return Math.sqrt(numerator/r2); //root G*M/r
    }

    /**
     * Performs one iteration of calculations for all bodies on the screen, setting the net force and calling update
     * @param none
     * @return none
     */
    private static void tick(){ //Performs N^2 + N iterations
        for (int i = 0; i < bodies.size(); i++) { 
            bodies.get(i).setfx(0.0); //reset forces for new iteration
            bodies.get(i).setfy(0.0);
            //2 loops = bad for processor
            for (int j = 0; j < bodies.size(); j++) { 
                if (i != j && !(bodies.get(i) instanceof Star)){ //a body cannot accelerate itself, and stars cannot be accelerated
                    bodies.get(i).addForce(bodies.get(j)); //accelerate body i by body j

                    /** //Absolutely filled with bugs, do not use (collision detection is inconsistent, bodies don't bounce properly or at all 
                     * //also conflicts with normal simulation, can cause strange alterations or destruction of orbits or completely void velocities

                    if(collision){
                    if(collide(bodies.get(i),bodies.get(j))){
                    Body a=bodies.get(i);
                    Body b=bodies.get(j);
                    a.setvx(-a.getvx());
                    a.setvy(-a.getvy());
                    b.setvx(-a.getvx());
                    b.setvy(-a.getvy());
                    }
                    }*/

                }
            }
        }
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).update(); //update velocities and position
        }
    }

    /**
     * Places a new body in a circular orbit at the point the user clicks, given it is not on the centre star
     * @param MouseEvent - Object from event interface that has the mouse location
     * @return none
     */
    public void mouseClicked(MouseEvent e){
        if(! (Math.abs(e.getX()-centerx)<36 && Math.abs(e.getY()-centery)<36) ){ // only run if body is being placed some distance from the sun
            if(!star){ //is it a planet?
                double x = (e.getX()-centerx)*invScale; //places a body at mouse point
                double y = (e.getY()-centery)*invScale; 
                double v = circlev(x,y); //calculate magnitude of velocity

                double absangle = Math.atan(Math.abs(y/x)); //calculate absolute angle of the body
                double theta= Math.PI/2-absangle; //calculate relative angle useful for calculations
                double vx   = -1*Math.signum(y)*Math.cos(theta)*v; //calculate x vector
                double vy   = Math.signum(x)*Math.sin(theta)*v; //calculate y vector
                //Orient a random 2D circular orbit direction
                if (Math.random() <=.5) {
                    vx=-vx;
                    vy=-vy;
                } 

                double mass = Math.random()*solMass*10+1e20; //random mass
                int img=(int)(Math.random()*6)+1;// random image number, 1-6 inclusive
                bodies.add( new Planet(x, y, vx, vy, mass,img));
            }else if(dynamic){ //is it a dynamic star?
                double x = (e.getX()-centerx)*invScale; //places a body at mouse point
                double y = (e.getY()-centery)*invScale; 
                bodies.add(new Planet(x,y,0,0,1e6*solMass,0));
            }else{ //it is a static star.
                double x = (e.getX()-centerx)*invScale; //places a body at mouse point
                double y = (e.getY()-centery)*invScale; 
                bodies.add(new Star(x,y,1e6*solMass,0));
            }
        }
    }

    public class ButtonListener implements ActionListener{
        /**
         * Performs select actions for certain button presses
         * @param button press
         * @return none
         */
        public void actionPerformed(ActionEvent evt) 
        {
            // Get label of the button clicked 
            String arg = evt.getActionCommand();    
            if (arg.equals("Restart")) /**restarts sim*/
            {
                pause=false; //unpause sim
                b6.setLabel("Static"); //resets sim to static stars
                dynamic=false;
                N = Integer.parseInt(t1.getText());
                if (N>100) { //the user may not simulate more than 100 bodies
                    t1.setText("100");
                    N=100;
                }
                bodies=new ArrayList<Body>(); //create new universe, garbage collection will delete old arraylist (i think)
                addBodies(N+1); //add N bodies +1 reserved for sun
                bodies.set(0, new Star(0,0,1e6*solMass,0)); //add sun
                Planet.setmult(1);
                repaint();
            }else if(arg.equals("Add")){ /**adds N bodies*/
                N = Integer.parseInt(t1.getText());
                if (N>100) { //the user may add 100 bodies repeatedly if they so please, but it will break the sim eventually
                    t1.setText("100");
                    N=100;
                }

                addBodies(N);
                repaint();
            }else if (arg.equals("Pause")) {/**Stop iterating physics*/
                pause=!pause;
            }else if (arg.equals("REMOVE STARS")) { /**Remove all stars, Increased forces to provide interesting interactions (not accurate) */
                for(int i=bodies.size();i>=0;i--){
                    if(bodies.get(i) instanceof Star){
                        bodies.remove(i);
                    }
                }
                Planet.setmult(10000); //Increases all forces by 1e4 to make the otherwise not very massive planets accelerate towards each other
                //This is necessary because the simulation makes liberties with inertia (much higher than most real solar systems)

            }else if (arg.equals("Add Star")) { /**Toggle Button and star boolean */
                b5.setLabel("Add Planet");
                star=false;
            }else if (arg.equals("Add Planet")) { /*See above */
                b5.setLabel("Add Star");
                star=true;
            }else if (arg.equals("Static")) { /**Toggle Button and dynamicy boolean */
                b6.setLabel("Dynamic");
                dynamic=true;
                for(int i=0;i<bodies.size();i++){ //make stars dynamic by making them planets
                    if(bodies.get(i) instanceof Star){
                        Body b=bodies.get(i);
                        bodies.set(i,new Planet(b.getx(),b.gety(),0,0,b.getMass(),0)); //convert stars to planets
                    }
                }
            }
            else if (arg.equals("Dynamic")) { /*See above */
                b6.setLabel("Static");
                dynamic=false;
                for(int i=0;i<bodies.size();i++){ //make stars dynamic by making them planets
                    if(bodies.get(i).getimg()==0){//If it has a star image, it is a star, no exceptions (hopefully)
                        Body b=bodies.get(i);
                        bodies.set(i,new Star(b.getx(),b.gety(),b.getMass(),0)); //convert planets to stars
                    }
                }
            }
        }
    }

    public  void mousePressed(MouseEvent e){}

    public  void mouseEntered(MouseEvent e){}

    public  void mouseExited(MouseEvent e){}

    public  void mouseReleased(MouseEvent e){}

    /** //This does not seem to work properly nor is it used currently
     * Checks if two bodies are colliding by calculating their distance and comparing to their radii
     * @param - 2 bodies being checked for collision
     * @return boolean - whether the bodies have collided or not
     */
    private static boolean collide(Body star, Body planet){
        double d=distance(star,planet);
        if(d<24){//one body is intersecting with the other
            return true;
        }
        return false;
    }

    /**
     * Calculates the distance between two bodies
     * This method was not given a tester due to it being an application of a mathematical equation
     * @param Body - 2 bodies being calculated between
     * @return double - distance between bodies
     */
    public static double distance(Body star, Body planet){
        return (  Math.sqrt( Math.pow((planet.getx()*scale-star.getx()*scale),2) + Math.pow((planet.gety()*scale-star.gety()*scale),2) )  );
    }
}
