<template>
  <a-modal
    title="资金操作"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    cancelText="关闭"
    style="top:20px;"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">

        <a-form-item>
          <a-alert :message="'用户：' + model.userName + ',  可提现金额：' + avaliableAmount + ' 元'" type="info" showIcon/>
        </a-form-item>

        <a-form-item label="用户账号" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="false">
          <a-input placeholder="请输入用户账号" v-decorator="[ 'userName', {}]" :readOnly="true" />
        </a-form-item>

        <a-form-item label="金额" :labelCol="labelCol" :wrapperCol="wrapperCol" hasFeedback>
          <a-input-number  v-decorator="[ 'amount', validatorRules.amount]"/>
        </a-form-item>
        <a-form-item label="系统订单号" :labelCol="labelCol" :wrapperCol="wrapperCol" hasFeedback>
          <a-input  v-decorator="[ 'orderId', validatorRules.orderId]"/>
        </a-form-item>
        <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" hasFeedback>
          <a-input  v-decorator="[ 'remark', validatorRules.remark]"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction, getAction,postAction } from '@/api/manage'


  export default {
    name: 'UserAmountModal',
    data() {
      return {
        visible: false,
        confirmLoading: false,
        confirmDirty: false,
        avaliableAmount: 0,
        url: {
          getMemberAvailableAmount: '/sys/userAmountEntity/getMemberAvailableAmount',
          adjust: '/sys/user/adjust'
        },
        validatorRules: {
          amount: {
            rules: [{
              required: true, message: '请输入金额!'
            }
              // {
              //   validator: this.validateAmount()
              // }
            ]
          },
          remark: {
            rules: [{
              required: true, message: '请输入备注!'
            }
            ]
          },
          orderId: {
            rules: [{
              required: true, message: '请输入订单号!'
            }
            ]
          }
        },

        model: {},

        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        form: this.$form.createForm(this)
      }
    },
    created() {
      console.log('created')
    },

    methods: {
      show(username) {
        this.form.resetFields()
        this.visible = true
        this.model.userName = username
        this.$nextTick(() => {
          this.form.setFieldsValue({ userName: username })
        })
        this.getMemberAvailableAmount(username)

      },
      close() {
        this.$emit('close')
        this.visible = false
        this.disableSubmit = false
      },
      handleSubmit() {
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            this.confirmLoading = true
            let formData = Object.assign(this.model, values)
            console.log(formData)
            postAction(this.url.adjust, formData).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.$emit('ok')
              } else {
                this.$message.warning(res.message)
              }
            }).finally(() => {
              this.confirmLoading = false
              this.close()
            })
          }
        })
      },
      handleCancel() {
        this.close()
      },

      handleConfirmBlur(e) {
        const value = e.target.value
        this.confirmDirty = this.confirmDirty || !!value
      },
      getMemberAvailableAmount(username) {
        getAction(this.url.getMemberAvailableAmount, {username}).then((res) => {
          if (res.success) {
            this.avaliableAmount = res.result
          } else {
            console.log(res.message)
            this.$message.error(res.message)
          }
        })
      },
    }
  }
</script>