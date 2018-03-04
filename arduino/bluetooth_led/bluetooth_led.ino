cjar command;
String string;
boolean ledon = false;
#define led 5
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(led,OUTPUT);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available()>0){
    string = "";
  }
  while(Serial.availble()>0{
    command = ((byte)Serial.read());
    if(command==":"){
      break;
    }else{
      string += command;
    }

    delay(1);
  }

  if(string == "TO"){
    ledOn();
    ledon = true;
  }
  if(string=="TF"){
    ledOff();
    ledon = false;
    Serial.println(string);
  }

  if((string.toInt()>=0)&&(string.toInt()<=255)){
    if(ledon==true){
      analogWrite(led, string.toInt());
      Serial.println(string); //debug
      delay(10);
    }
  }
}

void ledOn(){
  analogWrite(led,255);
  delay(10);
}

void ledOff(){
  analogWrite(led,0);
  delay(10);
}


