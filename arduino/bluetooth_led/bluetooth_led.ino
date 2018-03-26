//char command;
//String string;
char inbyte = 0;
boolean ledon = false;
#define led 13
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(led,OUTPUT);
  
}

void loop() {
  // put your main code here, to run repeatedly:

  sendAndroidValues();
  //when serial values have been received this will be true
  if (Serial.available() > 0)
  {
    inbyte = Serial.read();
    if (inbyte == '0')
    {
      //LED off
      ledOff();
      //digitalWrite(led, LOW);
    }
    if (inbyte == '1')
    {
      //LED on
      //digitalWrite(led, HIGH);
      ledOn();
    }
  }
  //delay by 2s. Meaning we will be sent values every 2s approx
  //also means that it can take up to 2 seconds to change LED state
  delay(2000);
//  if (Serial.available()>0){
//    Serial.println("There is info available");
//    string = "";
//  }
//  while(Serial.available()>0){
//    command = ((byte)Serial.read());
//    if(command==':'){
//      break;
//    }else{
//      string += command;
//    }
//
//    delay(1);
//  }
//
//  if(string == "TO"){
//    Serial.println("I'm turned on");
//    ledOn();
//    ledon = true;
//  }
//  if(string=="TF"){
//    ledOff();
//    ledon = false;
//    Serial.println(string);
//  }

//  if((string.toInt()>=0)&&(string.toInt()<=255)){
//    if(ledon==true){
//      analogWrite(led, string.toInt());
//      Serial.println(string); //debug
//      delay(10);
//    }
//  }

}

void ledOn(){
  analogWrite(led,255);
  delay(10);
}

void ledOff(){
  analogWrite(led,0);
  delay(10);
}

//sends the values from the sensor over serial to BT module
void sendAndroidValues()
 {
  int x = random(1000,2000);
  //puts # before the values so our app knows what to do with the data
  Serial.print('#');
  //for loop cycles through 4 sensors and sends values via serial

  Serial.print(x);
  //technically not needed but I prefer to break up data values
  //so they are easier to see when debugging
  
 Serial.print('~'); //used as an end of transmission character - used in app for string length
 Serial.println();
 delay(10);        //added a delay to eliminate missed transmissions
}

