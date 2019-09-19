<template>
  <div>
    <a-modal
      :title="title4add"
      :width="800"
      :visible="visible4Add"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="close4Add"
      cancelText="关闭">

      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item
            label="谷歌验证码">
            <a-input placeholder="谷歌验证码" style="width:200px;"  v-model="googleCode"/>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
    <google ref="google"></google>
  </div>
</template>

<script>
  import {getAction,httpAction} from '@/api/manage'
  export default {
    name: "UserChannelModal",
    data() {
      return {
        googleCode: '',
        model: {},
        data:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        url: {
          queryChannelByUserName: "/pay/userChannelEntity/queryChannelByUserName",
          channel: "/pay/channelEntity/channel",
          deleteUserChannel:"/pay/userChannelEntity/deleteUserChannel",
          addUserChannel:"/pay/userChannelEntity/add",
        },
      }
    },
    mounted:function () {
    },
    methods: {
      detail:function(record) {
        this.visible = true;
        var params = {username:record.username};//查询条件
        getAction(this.url.queryChannelByUserName,params).then((res)=>{
          if(res.success){
          this.data = res.result;
        }else{
        }
      })
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
          console.log(values);
          that.confirmLoading = true;
          let formData = Object.assign(this.model, values);
          formData.userName=this.userName;
          console.log(formData);
          //时间格式化
          httpAction(this.url.addUserChannel,formData,"post").then((res)=>{
            if(res.success){
            that.$message.success(res.message);
            that.$emit('ok');
          }else{
            that.$message.warning(res.message);
          }
        }).finally(() => {
            that.confirmLoading = false;
          that.close4Add();
        })
        }
      })
      },
      close() {
        this.$emit('close');
        this.visible = false;
      },
      close4Add(){
        this.$emit('close');
        this.visible4Add = false;
      },
      handleCancel() {
        this.close()
      },
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px!important;
  }
</style>