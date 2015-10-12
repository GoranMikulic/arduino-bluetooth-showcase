int ledPin = 9;  // analogWrite() works at pin 9
int state = 0;
int flag = 0; // return state only once

void setup() {
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);
    Serial.begin(9600); // Default connection rate for the bluetooth module
}
void loop() {
    //if some data is sent, read it and save it in the state variable
    if(Serial.available() > 0){
      state = Serial.read();
      flag=0;
    }

    // if the state is 0 the led will turn off
    if (state == '0') {
        digitalWrite(ledPin, LOW);
        if(flag == 0){
          Serial.println("OFF");
          Serial.println(state);
          flag = 1;
        }
    }
    // if the state is 1 the led will turn on
    else if (state > '0') {
        //digitalWrite(ledPin, HIGH);
        analogWrite(ledPin, state);
        if(flag == 0){
          Serial.println("ON");
          Serial.println(state);
          flag = 1;
        }
    }
}
