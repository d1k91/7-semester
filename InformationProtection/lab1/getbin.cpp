//Перевод дестичного числа в двоичную форму

#include<stdlib.h>
#include<iostream>
#include<math.h>

using namespace std;


int main()
{
  int x;
  int last_bit;
  cout<<"Enter x"<<endl;
  cin>>x;
   cout<<"Binary form is:"<<endl;
 while (x!=0) 
 {
   last_bit=x&1;
   cout<<last_bit;      
    x=x>>1;   
 }
   cout<<endl; 
    
system("pause");    
return 0;
}
