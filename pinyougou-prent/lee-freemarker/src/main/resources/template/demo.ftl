
<html>
<meta charset="UTF-8">
<#--引入include,使用的也是相对路径-->
<#include "header.ftl"/>
<body>
<#--注释 ，${}表示插值-->

hello ${name}
<br>
<#--#assign表示定义一个变量-->
<#assign linkman="周先生">
联系人：${linkman}

<#--定义对象类型  json格式：{}-->
<#assign info={"mobile":"13301231212",'address':'北京市昌平区王府街'} >
电话：${info.mobile}  地址：${info.address}

<br>

<#--如果怎样，否则怎样-->
<#if success==true>
    你已通过实名认证
<#else>
    你未通过实名认证
</#if>

<br>

<#--#list  循环遍历使用  ?c 去除-->
<#list goodsList as goods>
    ${goods_index+1} 商品名称： ${goods.name} 价格：${goods.price}<br>
</#list>

<#--内律函数  -->
  总共有${list?size}条记录

<br>

<#--JSON字符串转对象-->
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#--eval也是一个内置的函数-->`
<#assign data=text?eval />
开户行：${data.bank}  账号：${data.account}
<br>

只显示日期为: ${date?date}<br>
只显示时间为: ${date?time}<br>
日期时间都显示为: ${date?datetime}<br>
自定义日期格式处理：${date?string("yyyy/MM/dd HH:mm:ss")}

<br>
<#--空值的处理-->
${nullKey!"如果默认没有值，我就是默认值"}

<br>
<#--判断是否空值-->
<#if aaa??>
    aaa变量存在
<#else>
    aaa变量不存在
</#if>

</body>

</html>
