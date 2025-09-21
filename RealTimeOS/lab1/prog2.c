#include <stdio.h>
#include <termios.h>
#include <unistd.h>


int main(){
	struct termios old, new;
	
	tcgetattr(0, &old);
	
	new = old;
	
	new.c_lflag &= ~(ICANON | ECHO);
	tcsetattr(0, TCSANOW, &new);
	
	int c;
	while ((c = getchar()) != 3){
		printf("Code : %d\n", c);
	}
	
	tcsetattr(0, TCSANOW, &old);
	
	return 0;
	
}