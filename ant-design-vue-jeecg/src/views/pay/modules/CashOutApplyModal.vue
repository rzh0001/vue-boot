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
          label="提现金额">
          <a-input-number v-decorator="[ 'amount', {}]" />
        </a-form-item>
        <a-form-item label="银行卡" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!bankCardDisabled" >
          <a-select
            mode="single"
            style="width: 100%"
            placeholder="请选择银行卡"
            v-model="selectedBankCard">
            <a-select-option v-for="(bankCard,bankCardindex) in bankCardList" :key="bankCardindex.toString()" :value="bankCard.id">
              {{ bankCard.bankName + '|' +  bankCard.accountName + '|'  +  bankCard.cardNumber}}
            </a-select-option>
          </a-select>
        </a-form-item>

<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="银行卡表ID">-->
<!--          <a-input placeholder="请输入银行卡表ID" v-decorator="['bankCardId', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="银行名称">-->
<!--          <a-input placeholder="" disabled  v-decorator="['bankName', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="分支行">-->
<!--          <a-input placeholder="请输入分支行" v-decorator="['branchName', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="账户名">-->
<!--          <a-input placeholder="请输入账户名"  v-decorator="['accountName', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="卡号">-->
<!--          <a-input placeholder="请输入卡号" v-decorator="['cardNumber', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="发起时间">-->
<!--          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'applyTime', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="审批时间">-->
<!--          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'approvalTime', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="状态">-->
<!--          <a-input placeholder="请输入状态(1-待审核;2-通过;3-拒绝)" v-decorator="['status', {}]" />-->
<!--        </a-form-item>-->
<!--        <a-form-item-->
<!--          :labelCol="labelCol"-->
<!--          :wrapperCol="wrapperCol"-->
<!--          label="删除状态">-->
<!--          <a-input placeholder="请输入删除状态（0，正常，1已删除）" v-decorator="['delFlag', {}]" />-->
<!--        </a-form-item>-->
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import { queryBankCard } from '@/api/api'

  export default {
    name: "CashOutApplyModal",
    data () {
      return {
        bankCardDisabled: false,
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

        bankCardList:[],
        selectedBankCard:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          add: "/pay/cashOutApply/add",
          edit: "/pay/cashOutApply/edit",
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
        this.initialBankCardList();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'userId','username','amount','bankCardId','bankName','branchName','accountName','cardNumber','status','delFlag'))
		  //时间格式化
          this.form.setFieldsValue({applyTime:this.model.applyTime?moment(this.model.applyTime):null})
          this.form.setFieldsValue({approvalTime:this.model.approvalTime?moment(this.model.approvalTime):null})
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
            formData.bankCardId = this.selectedBankCard;
            //时间格式化
            formData.applyTime = formData.applyTime?formData.applyTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.approvalTime = formData.approvalTime?formData.approvalTime.format('YYYY-MM-DD HH:mm:ss'):null;
            
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

      initialBankCardList(){
        queryBankCard().then((res)=>{
          if(res.success){
            this.bankCardList = res.result;
          }else{
            console.log(res.message);
          }
        });
      },

    }
  }
</script>

<style lang="less" scoped>

</style>