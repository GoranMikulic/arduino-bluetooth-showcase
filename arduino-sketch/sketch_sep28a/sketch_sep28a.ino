int ledPin = 9;  // use the built in LED on pin 13 of the Uno
int state = 0;
int flag = 0;        // make sure that you return the state only once
int brightness = 0;

void setup() {
    // sets the pins as outputs:
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);
    Serial.begin(9600); // Default connection rate for my BT module
}
void loop() {
    //if some data is sent, read it and save it in the state variable
    
    if(Serial.available() > 0){
      state = Serial.read();
      Serial.println(state);
      flag=0;
    }
    // if the state is 0 the led will turn off
    if (state == '0') {
        digitalWrite(ledPin, LOW);
        if(flag == 0){
          Serial.println("LED: off");
          flag = 1;
        }
    }
    // if the state is 1 the led will turn on
    else if (state == '1') {
        digitalWrite(ledPin, HIGH);
        analogWrite(ledPin, brightness);
        if(flag == 0){
          Serial.println("LED: on");
          flag = 1;
        }
    }
    else if (state > '1') {
        //brightness = state;
        digitalWrite(ledPin, HIGH);
        //analogWrite(ledPin, brightness);
        if(flag == 0){
          Serial.println("LED: on");
          flag = 1;
        }
    }
}