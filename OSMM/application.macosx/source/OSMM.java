import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class OSMM extends PApplet {



String[] noteNames={"A","B","B#","C","C#","D","D#","E","F","F#","G","G#"};

TriOsc[] osc;
int polyphony=88;

Note[][] midi;

int bpm=120;
int playhead=0;
PVector lastPos=new PVector(-1,-1);

float xScale=10;
float yScale;

float noteLen=1;

boolean playing=false;
boolean smoothNotes=false;

public void setup(){
  
  midi=new Note[128][88];
  osc=new TriOsc[polyphony];
  initMidi();
  initOsc();
  yScale=height/(float)midi[0].length;
}

public void draw(){
  //background(0);
  if(mousePressed){
    if(lastPos.x!=mouseX/xScale||lastPos.y!=mouseY/yScale){
      midi[floor(mouseX/xScale)][floor(mouseY/yScale)].enabled=!midi[floor(mouseX/xScale)][floor(mouseY/yScale)].enabled;
      lastPos.x=mouseX/xScale;
      lastPos.y=mouseY/yScale;
    }
  }
  if(!playing){
    for(int x=0;x<midi.length;x++){
      for(int y=0;y<midi[0].length;y++){
        stroke(80);
        fill((midi[x][y].enabled)?225:(isScale(y))?75:50);
        rect(x*xScale,y*yScale,xScale,yScale);
      }
    }
  }
  if(playing){
    int[] notes=getMidiAt(playhead);
    if(notes.length>0){
      println(noteNames[notes[0]%12]);
      for(int i=0;i<polyphony&&i<notes.length;i++){
        osc[i].freq(calcFreq(notes[i]));
        osc[i].play();
      }
    }
    fill(255,128);
    rect(playhead*10,0,10,height);
    delay(round(((60f/bpm)/2)*1000));
    for(int i=0;i<notes.length&&i<polyphony;i++){
      if(!smoothNotes){
        osc[i].stop();
      }
    }
    playhead++;
    if(playhead==midi.length){
      playing=false;
      polyphony=1;
    }
  }
  surface.setTitle("O.S.M.M. FPS: "+frameRate);
}

public boolean isScale(int y){
  String name=noteNames[y%12];
  return name=="C"||name=="D"||name=="E"||name=="F"||name=="G"||name=="A"||name=="B";
}

public float calcFreq(int y){
  float diff=49f-y;
  float a=pow(2f,diff/12f);
  return a*440f;
}

public void mouseReleased(){
  lastPos.x=-1;
  lastPos.y=-1;
}

public void keyPressed(){
  if(key==' '){
    playhead=0;
    playing=!playing;
    for(int i=0;i<osc.length;i++){
      osc[i].stop();
    }
    println(playing);
  }
  if(key=='p'){
    for(int x=0;x<midi.length;x++){
      for(int y=0;y<midi[0].length;y++){
        midi[x][y].enabled=(x%2==1&&y%2==1)?!midi[x][y].enabled:midi[x][y].enabled;
        if(x==y){
          midi[x][y].enabled=!midi[x][y].enabled;
        }
      }
    }
  }
}

public int[] getMidiAt(int at){
  ArrayList<Integer> notes=new ArrayList<Integer>();
  for(int i=0;i<midi[0].length;i++){
    if(midi[at][i].enabled){
      notes.add(i);
    }
  }
  return listToArray(notes);
}

public int[] listToArray(ArrayList<Integer> l){
  int[] array=new int[l.size()];
  
  for(int i=0;i<array.length;i++){
    array[i]=l.get(i);
  }
  
  return array;
}

public void initMidi(){
  for(int x=0;x<midi.length;x++){
    for(int y=0;y<midi[0].length;y++){
      midi[x][y]=new Note();
    }
  }
}

public void initOsc(){
  for(int i=0;i<osc.length;i++){
    osc[i]=new TriOsc(this);
    osc[i].amp(0.1f);
  }
}
public class Note{
  public boolean enabled=false;
  
  public Note(){
    
  }
}
  public void settings() {  size(1280,720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "OSMM" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
