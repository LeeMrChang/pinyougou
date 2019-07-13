var app = new Vue({
    //1.与前端对应的标识
    el:"#app",
    //2.放置属性的地方
    data:{
        pages:15,
        pageNo:1,
        cartList:[],  // 定义的从后台获取到的购物车列表的数据的变量
        entity:{},
        totalMoney:0,  //设置变量，总小计的金额
        totalNum:0,  //总数量
        ids:[],
        searchEntity:[],
        username:'',
        addressList:[]    //定义一个变量，用户的地址集合
    },
    //3.放置方法的地方
    methods:{

        //此函数表示页面加载时，从后台根据用户从cookie或者redis中获取到购物车列表的数据，然后将值赋予给购物车列表的变量
        findCartList: function () {
            //变量初始化都是0
            this.totalMoney = 0;
            this.totalNum = 0;

            axios.get('/cart/findCartList.shtml').then(function (response) {

                //获取购物车列表数据
                app.cartList = response.data;
                //获取到购车列表的数据
                var obj = app.cartList;

                //遍历购物车的明细列表
                for (var i = 0; i <obj.length; i++) {
                    //每一个购物车对象
                        var cart = obj[i];
                        //还需遍历购物车对象
                    for (var j = 0; j <cart.orderItemList.length ; j++) {
                        //累计总数量
                        app.totalNum+=cart.orderItemList[i].num;

                        //累计总金额
                        app.totalMoney+=cart.orderItemList[i].totalFee;

                    }
                }




            });
        },

        /**
         *       向已有的购物车添加商品，或者减去商品
         * @param itemId  商品id
         * @param num  商品数量
         */
        addGoodsToCartList:function (itemId,num) {
            //向后台发送请求
            axios.get('/cart/addGoodsToCartList.shtml', {
                params: {
                    itemId:itemId,  //这里是传递参数
                    num:num
                }
            }).then(function (response) {

                if(response.data.success){
                    //添加成功，刷新页面
                    app.findCartList();
                }else{
                    //添加失败，响应错误信息
                    alert(response.data.message);
                }
            });
        },

        //页面加载的时候显示登录的用户名称
        getName:function () {
            //发送请求获取用户名称
            axios.get('/cart/getName.shtml').then(function (response) {

                app.username = response.data;

                if(app.username == 'anonymousUser'){
                        app.username = '';
                }
            })
        },

        //，当页面加载时，定义一个根据用户名查询用户地址的函数方法
        findAddressList:function () {

            axios.get('/address/findAddressListByUserId.shtml').then(
                function (response) {
                        alert("888")
                    app.addressList = response.data;
                }
            )
        }

    },

    //4.钩子函数，一些显示数据可初始化的地方
    created:function () {

        //页面加载时调用
        //this.findCartList();

        this.getName();

        this.findAddressList();
    }


});