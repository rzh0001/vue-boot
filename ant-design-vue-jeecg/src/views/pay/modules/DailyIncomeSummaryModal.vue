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
          label="用户id">
          <a-input placeholder="请输入用户id" v-decorator="['userId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="用户名">
          <a-input placeholder="请输入用户名" v-decorator="['username', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="用户名称">
          <a-input placeholder="请输入用户名称" v-decorator="['realname', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="总订单数">
          <a-input-number v-decorator="[ 'totalOrderCount', validatorRules.totalOrderCount ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="总订单金额">
          <a-input-number v-decorator="[ 'totalOrderAmount', validatorRules.totalOrderAmount ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="已付订单数">
          <a-input-number v-decorator="[ 'paidOrderCount', validatorRules.paidOrderCount ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="已付订单金额">
          <a-input-number v-decorator="[ 'paidOrderAmount', validatorRules.paidOrderAmount ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="未付订单数">
          <a-input-number v-decorator="[ 'unpaidOrderCount', validatorRules.unpaidOrderCount ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="未付订单金额">
          <a-input-number v-decorator="[ 'unpaidOrderAmount', validatorRules.unpaidOrderAmount ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="收入">
          <a-input-number v-decorator="[ 'feeIncome', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="代理ID">
          <a-input placeholder="请输入代理ID" v-decorator="['agentId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="代理帐号">
          <a-input placeholder="请输入代理帐号" v-decorator="['agentUsername', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="代理姓名">
          <a-input placeholder="请输入代理姓名" v-decorator="['agentRealname', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="介绍人ID">
          <a-input placeholder="请输入介绍人ID" v-decorator="['salesmanId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="介绍人帐号">
          <a-input placeholder="请输入介绍人帐号" v-decorator="['salesmanUsername', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="介绍人姓名">
          <a-input placeholder="请输入介绍人姓名" v-decorator="['salesmanRealname', {}]" />
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
    name: "DailyIncomeSummaryModal",
    data () {
      return {
        title:"操作",
        visible: false,
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
        totalOrderCount:{rules: [{ required: true, message: '请输入总订单数!' }]},
        totalOrderAmount:{rules: [{ required: true, message: '请输入总订单金额!' }]},
        paidOrderCount:{rules: [{ required: true, message: '请输入已付订单数!' }]},
        paidOrderAmount:{rules: [{ required: true, message: '请输入已付订单金额!' }]},
        unpaidOrderCount:{rules: [{ required: true, message: '请输入未付订单数!' }]},
        unpaidOrderAmount:{rules: [{ required: true, message: '请输入未付订单金额!' }]},
        },
        url: {
          add: "/pay/dailyIncomeSummary/add",
          edit: "/pay/dailyIncomeSummary/edit",
        },
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'userId','username','realname','totalOrderCount','totalOrderAmount','paidOrderCount','paidOrderAmount','unpaidOrderCount','unpaidOrderAmount','feeIncome','agentId','agentUsername','agentRealname','salesmanId','salesmanUsername','salesmanRealname'))
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