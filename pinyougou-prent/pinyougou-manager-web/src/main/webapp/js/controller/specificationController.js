﻿var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        //specification:{}代表一个规格选项表的对象
        //optionList:[]代表一个规格可能有多个选项，使用数组容器装
        entity:{specification:{},optionList:[{}]},
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/specification/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
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
            axios.get('/specification/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/specification/findPage.shtml',{params:{
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
            axios.post('/specification/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/specification/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        //判断是新增还是修改，有参数时修改，没参数时新增
        save:function () {
            if(this.entity.specification.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/specification/findOne/'+id+'.shtml').then(function (response) {
                //服务器响应的查询结果
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        dele:function () {
            axios.post('/specification/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },

        //当点击新增按钮的时候调用，向已有的数组中添加一个{} (json对象)
        addTableRow:function () {
            //表示向数组中添加一个对象
            this.entity.optionList.push({});
        },

        //删点击删除按钮的时候，向已有的数组中删除对应的那个对象
        removeTableRow:function (index) {
            //第一个参数表示索引号(下标)
            //第二个参数表示要删除的个数
            this.entity.optionList.splice(index,1);
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
