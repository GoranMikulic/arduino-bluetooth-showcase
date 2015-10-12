int ledPin = 9;  // analogWrite() works at pin 9
int state = 0;
int flag = 0; // return state only once
int ledStateOff = 300;

void setup() {
    pinMode(ledPin, OUTPUT);
    digitalWrite(ledPin, LOW);
    Serial.begin(9600); // Default connection rate for the bluetooth module
}

void loop() {
    state = readBufferValue();
    // if the state is 0 the led will turn off
    if (state == ledStateOff) {
        digitalWrite(ledPin, LOW);
        if(flag == ledStateOff){
          flag = 1;
        }
        Serial.println(state);
    }
    // if the state is 1 the led will turn on
    else if (state > '0') {
        analogWrite(ledPin, state);
        if(flag == 0){
          flag = 1;
        }
        Serial.println(state);
    }
}

int readBufferValue() {
  if(Serial.available() > 0){
      char incomingByte;
      int integerValue = 0;
      while(1) {            // force into a loop until 'n' is received
        incomingByte = Serial.read();
        if (incomingByte == '\n') break;   // exit the while(1), we're done receiving
        if (incomingByte == -1) continue;  // if no characters are in the buffer read() returns -1
        integerValue *= 10;  // shift left 1 decimal place
        // convert ASCII to integer, add, and shift left 1 decimal place
        integerValue = ((incomingByte - 48) + integerValue);
      }
      return integerValue;
  }
}
