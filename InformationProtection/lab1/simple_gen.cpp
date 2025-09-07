//Генерация простого числа

#include<stdlib.h>
#include<iostream>
#include<math.h>

using namespace std;

bool isPrime(int p)
{
   if (p<=1) return false;
     
   int b=(int)pow(p,0.5); 
    
   for(int i=2;i<=b;++i)
   {
      if ((p%i)==0) return false;        
   }     
     
   return true;  
     
}


int main()
{
  int x;
  
 // cin >>x;
  
 // cout<<isPrime(x)<<endl;
  
  
  
  srand(time(NULL));
 
   int range=100;
  do{
      x=rand()%range;
    }while(isPrime(x)==false);
    
 cout<<x<<endl;    
  
    
system("pause");    
return 0;
}
