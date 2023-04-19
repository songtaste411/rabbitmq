
![image](https://user-images.githubusercontent.com/32946853/232985119-ffef01e1-6ad4-4902-bc7a-c7d537147b3f.png)
Mq的流程分析
1.在外卖平台下单后，给餐馆服务发消息
![image](https://user-images.githubusercontent.com/32946853/232985041-d4a8274e-579a-446e-8ee9-2efad1015572.png)

2.餐馆服务收到后，更改订单状态，给订单服务发消息
![image](https://user-images.githubusercontent.com/32946853/232985227-ca19342d-dc46-4dce-b337-ec8c1ca9ad4d.png)


过程1和2用的是同一个交换机,所以流程可以简化为：
![image](https://user-images.githubusercontent.com/32946853/232985348-1121217b-a480-4d7a-a96c-b126d5a75052.png)


3.订单服务收到后，给骑手服务发消息
4.骑手服务收到消息，更改订单状态，给订单服务发消息
![image](https://user-images.githubusercontent.com/32946853/232985520-8d2c81e1-6913-4a02-8b6c-7b1a6d385a3b.png)


5.订单服务收到后，给结算服务发消息
6.结算服务收到消息，生成结算单，给订单服务发消息
![image](https://user-images.githubusercontent.com/32946853/232985668-e1bf0652-fdac-4b1c-aeea-4dfa1cc81319.png)

不同的是交换机改成了fanout模式，这里如果结算队列如果再和订单队列使用同一个交换机，那么结算服务会收到自己给自己发送的消息，造成消息循环，这结果是错的

所以，我们用不同的交换机
![image](https://user-images.githubusercontent.com/32946853/232985750-fc7e82ff-d7fe-4ed7-ae61-0a6fb2afbda3.png)



