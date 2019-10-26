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

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单号" :v-show="false">
          <a-input placeholder="请输入订单号" v-decorator="['orderId', validatorRules.orderId ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="外部订单号" :v-show="false">
          <a-input placeholder="请输入外部订单号" v-decorator="['outerOrderId', validatorRules.outerOrderId ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户id">
          <a-input placeholder="请输入用户id" v-decorator="['userId', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户">
          <a-input placeholder="请输入用户" v-decorator="['userName', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户昵称">
          <a-input placeholder="请输入用户昵称" v-decorator="['userRealname', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="商户编号">
          <a-input placeholder="请输入商户编号" v-decorator="['merchantId', validatorRules.merchantId ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单金额">
          <a-input-number v-decorator="[ 'amount', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝">
          <a-input placeholder="请输入订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝" v-decorator="['status', validatorRules.status ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="银行卡ID">
          <a-input placeholder="请输入银行卡ID" v-decorator="['bankcardId', validatorRules.bankcardId ]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户类型(1-对私;2-对公)">
          <a-input placeholder="请输入账户类型(1-对私;2-对公)" v-decorator="['accountType', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账户名">
          <a-input placeholder="请输入账户名" v-decorator="['accountName', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="卡号">
          <a-input placeholder="请输入卡号" v-decorator="['cardNumber', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="银行名称">
          <a-input placeholder="请输入银行名称" v-decorator="['bankName', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开户行全称">
          <a-input placeholder="请输入开户行全称" v-decorator="['branchName', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
          <a-input placeholder="请输入备注" v-decorator="['remark', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成功时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'successTime', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="代理ID">
          <a-input placeholder="请输入代理ID" v-decorator="['agentId', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="代理帐号">
          <a-input placeholder="请输入代理帐号" v-decorator="['agentUsername', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="代理姓名">
          <a-input placeholder="请输入代理姓名" v-decorator="['agentRealname', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="介绍人ID">
          <a-input placeholder="请输入介绍人ID" v-decorator="['salesmanId', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="介绍人帐号">
          <a-input placeholder="请输入介绍人帐号" v-decorator="['salesmanUsername', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="介绍人姓名">
          <a-input placeholder="请输入介绍人姓名" v-decorator="['salesmanRealname', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="操作IP">
          <a-input placeholder="请输入操作IP" v-decorator="['ip', {}]" />
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
    name: "RechargeOrderModal",
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
          orderId:{rules: [{ required: true, message: '请输入订单号!' }]},
          outerOrderId:{rules: [{ required: true, message: '请输入外部订单号!' }]},
          merchantId:{rules: [{ required: true, message: '请输入商户编号!' }]},
          status:{rules: [{ required: true, message: '请输入订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝!' }]},
          bankcardId:{rules: [{ required: true, message: '请输入银行卡ID!' }]},
        },
        url: {
          add: "/df/rechargeOrder/add",
          edit: "/df/rechargeOrder/edit",
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
          this.form.setFieldsValue(pick(this.model,'orderId','outerOrderId','userId','userName','userRealname','merchantId','amount','status','bankcardId','accountType','accountName','cardNumber','bankName','branchName','remark','agentId','agentUsername','agentRealname','salesmanId','salesmanUsername','salesmanRealname','ip'))
          //时间格式化
          this.form.setFieldsValue({successTime:this.model.successTime?moment(this.model.successTime):null})
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
            formData.successTime = formData.successTime?formData.successTime.format('YYYY-MM-DD HH:mm:ss'):null;

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