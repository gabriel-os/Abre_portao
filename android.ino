#include <RCSwitch.h>

RCSwitch mySwitch = RCSwitch();

//variable for Serial input 
int input = 0; 

void setup() {
  // put your setup code here, to run once:
  
  //Start the serial monitor at 9600 baud  
  Serial.begin(9600);   

   // Transmitter is connected to Arduino Pin #10  
  mySwitch.enableTransmit(10);
  
  // Optional set protocol (default is 1, will work for most outlets)
  mySwitch.setProtocol(6);

  // Optional set pulse length.
  mySwitch.setPulseLength(320);
}

void loop() {
  //check if there's incoming data,  
  
  if(Serial.available()){    
      //if so, then read the incoming data.    
      String input = Serial.readString(); 

      if(input.length() >0){
          Serial.println("Foi2"); 
          Serial.println(input);
          mySwitch.send(input.toInt(), 28);
          Serial.println("ENVIADO!");    
        }

      
  }
}
