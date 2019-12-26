var baseUrl = parent.window.baseUrl || '../';

var queryUrl = baseUrl + "api/active/findPage";
var excelUrl = baseUrl + "api/active/excel";

var ajaxReq = parent.window.ajaxReq || "";


var myvue = new Vue({
	    el: '#app',
	    data: function(){
	    	return {
	    		activeTab: 'table',
				filters: {
					user: '',
					start: '',
					end: ''
				},
				list: [],
				total: 0,
				page: 1,
				rows: 10,
				listLoading: false,
				sels: [],
				preloading: false,
				
				typeOptions:[
					{label: "事件1", value: 1},
					{label: "事件2", value: 2},
					{label: "事件3", value: 3},
					{label: "事件4", value: 4},
					{label: "事件5", value: 5},
					{label: "事件6", value: 6},
					{label: "事件7", value: 7},
					{label: "事件8", value: 8},
					{label: "事件9", value: 9},
					{label: "事件10", value: 10},
				],
				
				user: ''
			}
		},
		methods: {
			formatDate: function(date){
				return parent.window.formatDate(date, 'yyyy-MM-dd HH:mm:ss');
			},
			handleSizeChange: function (val) {
				this.rows = val;
				this.getList();
			},
			handleCurrentChange: function (val) {
				this.page = val;
				this.getList();
			},
			query:function(){
				this.page = 1;
				this.getList();
			},
			//query
			getList: function () {
				var self = this;
				var params = {
					page: this.page,
					rows: this.rows
				};
				for ( var key in this.filters) {
					if(this.filters[key]){
						params[key] = this.filters[key];
					}
				}
				this.listLoading = true;
				ajaxReq(queryUrl, params, function(res){
					self.listLoading = false;
					self.handleResQuery(res, function(){
						self.total = res.total;
						self.list = res.data;
						/*if(self.page != 1 && self.total <= (self.page - 1) * self.rows){
							self.page = self.page - 1;
							self.getList();
						}*/
					});
				});
			},
			//reset
			reset: function(){
				this.filters = {
					user: '',
					start: '',
					end: ''
				};
				this.getList();
			},
			getExcel: function(){
				var params = "";
				for ( var key in this.filters) {
					if(this.filters[key]){
						params += "&"+key+"="+this.filters[key];
					}
				}
				parent.window.open(excelUrl + "?token=" + parent.window.loginToken + params);
			},
			
			selsChange: function (sels) {
				this.sels = sels;
			},
			//res
			toLoginHtml: function(){
                localStorage.removeItem('loginUser');
                parent.window.location.href = "login.html";
			},
			handleResQuery: function(res, success, failed){
				this.handleRes(false, res, success, failed);
			},
			handleRes: function(show, res, success, failed){
				if(res.code > 0){
					if(show){
						this.$message({
							message: '成功',
							type: 'success'
						});
					}
					if(typeof success == 'function'){
						success(res, this);
					}
				}else if(res.code == -111){
					this.$message({
						message: '未登录.',
						type: 'warning'
					});
					this.toLoginHtml();
				}else if(res.code == -201){
					this.$message({
						message: '没有权限.',
						type: 'warning'
					});
				}else{
					if(show){
						this.$message({
							message: '失败',
							type: 'warning'
						});
					}
					if(typeof failed == 'function'){
						failed(res, this);
					}
				}
			}
		},
		mounted: function() {
	      	this.user = JSON.parse(localStorage.getItem('loginUser'));
	  		if(this.user　==　null){
	  			parent.window.location.href = "login.html";
	  			return;
	  		}
			this.preloading = true;
			this.getList();
		}
	  });
	
	

