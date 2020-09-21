<template>
  <a-card
    @cancel="handleCancel" >

    <a-spin :spinning="confirmLoading">
      <a-form @submit="handleSubmit" :form="form">
        <a-form-item>
          <a-alert :message="'充值订单提交后，显示银行卡信息，请向匹配的银行卡打款！'" type="info" showIcon/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="充值金额">
          <a-input-number v-decorator="[ 'amount', validatorRules.amount]" :min="0"/>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="付款人">
          <a-input placeholder="请输入付款人，才能自动回调" v-decorator="['payer', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
          <a-input placeholder="请输入备注" v-decorator="['remark', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="成功时间" v-show="false">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'successTime', {}]" />
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="操作IP" v-show="false">
          <a-input placeholder="请输入操作IP" v-decorator="['ip', {}]" />
        </a-form-item>

        <a-form-item :wrapperCol="{span: 19, offset: 5}">
          <a-button type="primary" htmlType="submit" >生成订单</a-button>
        </a-form-item>
      </a-form>
    </a-spin>

  </a-card>
</template>

<script>
  import { getAction,postAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "Step1",
    components: {
    },
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
          orderId:{rules: [{ required: false, message: '请输入订单号!' }]},
          outerOrderId:{rules: [{ required: false, message: '请输入外部订单号!' }]},
          merchantId:{rules: [{ required: false, message: '请输入商户编号!' }]},
          status:{rules: [{ required: false, message: '请输入订单状态：0-已保存;1-已打款,待审核;2-已确认;3-审核拒绝!' }]},
          bankcardId:{rules: [{ required: true, message: '请输入银行卡ID!' }]},
          amount:{rules: [{ required: true, message: '请输入充值金额!' }]},
        },
        url: {
          add: "/df/rechargeOrder/add",
          edit: "/df/rechargeOrder/edit",
          getBankcard: "/df/rechargeOrder/getBankcard"
        },
        bankcard: {},
        text: "",
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit();
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
      handleCancel () {
        this.close()
      },
      nextStep () {
        this.$emit('nextStep')
      },

      handleSubmit (e) {
        e.preventDefault()
        this.form.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign(this.model, values);
            postAction(this.url.add,formData).then((res)=>{
              if(res.success){
                this.$message.success("生成订单成功");
                // that.$emit('ok');
                let record = res.result;
                // this.$refs.modalForm.show(record);
                this.$emit('message', record)

                this.$emit('nextStep')
              }else{
                that.$message.warning(res.message);
              }
            })
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>

</style>