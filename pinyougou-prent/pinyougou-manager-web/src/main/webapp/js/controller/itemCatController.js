﻿var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],   //根据商品分类表的id（商品分类表的ID为ParentID的父类ID）输入parentID去查询它对应的子信息
        entity:{parentId:0},//定义一个变量，此变量用来记录每次点击查询时类目父级的ID。
        ids:[],
        grade:1,   //表示面包屑的等级，一开始等级默认为1 ，总共分3级，这是一种面包屑导航模式
        entity_1:{},//变量1，这是点击第2级时，为此等级赋值，接收值的变量1
        entity_2:{},//变量2 ，这是点击第3级时，为此等级赋值，接收值得变量2
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/itemCat/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/itemCat/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/itemCat/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/itemCat/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    //添加成功，根据类目父级ID去查询展示
                    app.searchList({parentId:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/itemCat/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    //修改成功，根据类目父级ID去查询展示
                    app.searchList({parentId:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/itemCat/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/itemCat/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //根据商品分类表的id（商品分类表的ID为ParentID的父类ID）输入parentID去查询它对应的子信息
        findByParentId:function (parentId) {
            //1.发送异步请求给服务器，根据parentId查询对应的子ID的信息
            axios.get('/itemCat/findByParentId/'+parentId+'.shtml').then(
                function (response) {
                    //2.查询得到则将值赋值给list去做回显使用
                    app.list = response.data;
                }
            ).catch(function (error) {
                //打印错误信息
                console.log(error);
            })
        },

        //定义一个点击时影响变量值得方法，此方法一直都是在被点击时调用
        //p_entity是被点击时页面传过来的参数，等级的变化都是根据这个参数被点击的次数来判断的
        selectList:function (p_entity) {

            //如果当前的等级是1，变量1与变量2的值没有变化
            if(this.grade==1){
                this.entity_1={};
                this.entity_2={};
            }
            //如果当前是等级2时，为变量1赋值
            if(this.grade==2){
                this.entity_1=p_entity;
                this.entity_2={};
            }

            //如果当前是等级3时，为变量2赋值
            if(this.grade==3){
                this.entity_2=p_entity;
            }

            //每次点击时把值赋值给它
            this.entity.parentId = p_entity.id;
            //每次点击等级查询时，页面都会进行刷新，刷新每一等级的变量信息内容
            this.findByParentId(p_entity.id);
        }


    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);
        //初始化根据ParentId查询的到信息,一级类目的ID默认从0开始
        //this.findByParentId(0);

       //初始化根据ParentId查询的到信息,一级类目的ID默认从0开始
        this.selectList({id:0});
    }

})
