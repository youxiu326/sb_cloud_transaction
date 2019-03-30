
post localhost:7009/order/pay 

Content-Type  application/json

{
    "001":"12",
    "002":"2"
}


localhost:7008  选课服务

_task任务中的id一致的——
_接受消息时 注意去task中查看记录是否已经处理_
_发送消息时 通过version 乐观锁保持幂等性_

1.通过调用localhost:7009/order/pay  发送下订单请求，新增订单记录 新增选课task记录

2.订单服务 定时去查询选课task记录 如果发现有选课task记录 则发送选课消息至选课服务


3.选课服务监听 选课服务消息 收到消息后 先查询选课task history中有无该选课完成记录 如果没 新增选课记录，
并往选课task history中新增一条记录 最后再发送选课完成消息至订单服务

4.订单服务监听 选课完成服务消息 收到消息后 先查询选课task中有无该选课记录，如果有 则删除该选课task记录，
并新增选课task history记录

