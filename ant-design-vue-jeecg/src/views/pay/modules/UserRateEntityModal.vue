<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="通道">
          <select v-decorator="['channelCode', validatorRules.channelCode ]">
            <option v-for="option in channels" v-bind:value="option.channelCode">
              {{ option.channelName}}
            </option>
          </select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="高级代理名称">
          <a-input placeholder="请输入高级代理名称" v-decorator="['agentId', validatorRules.agentId ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="用户名">
          <a-input placeholder="请输入用户名" v-decorator="['userName', validatorRules.userName ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="被介绍人名称">
          <a-input placeholder="只有在设置介绍人费率的时候，才需要填" v-decorator="['beIntroducerName', validatorRules.beIntroducerName ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="费率">
          <a-input placeholder="请输入费率，如0.003" v-decorator="['userRate', validatorRules.userRate ]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "UserRateEntityModal",
    data () {
      return {
        title:"操作",
        visible: false,
        channels: [],
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        userName:{rules: [{ required: true, message: '请输入用户名!' }]},
        userRate:{rules: [{ required: true, message: '请输入费率!' }]},
          channelCode:{rules: [{ required: true, message: '请选择通道' }]},
        agentId:{rules: [{ required: true, message: '请输入高级代理名称!' }]},
        },
        url: {
          add: "/pay/userRateEntity/add",
          edit: "/pay/userRateEntity/edit",
          channel: "/pay/channelEntity/channel"
        },
      }
    },
    created () {
    },
    mounted:function () {
      this.channel();
    },
    methods: {
      channel(){
        httpAction(this.url.channel,null,'get').then((res)=>{
          if(res.success){
          this.channels = res.result;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'userId','userName','userRate','delFlag','createUser','updateUser','agentId','beIntroducerName'))
		  //时间格式化
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>

</style>