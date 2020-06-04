<template>
  <div>
    <a-modal
      :title="title"
      :width="800"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭">

      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item label="商户" style="width: 300px">
            <a-input placeholder="商户" style="width:200px;" readOnly=true v-model="userName"/>
          </a-form-item>
          <a-form-item label="产品" style="width: 300px">
            <a-select v-model="productCode" @change="getBusinessRelatedChannel">
              <a-select-option v-for="product of products"
                               :value="product.productCode" :key="product.productCode">{{product.productName}}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="请选择要关联的通道" style="width: 300px">
            <a-select v-model="channelCode" @change="getBusinesses">
              <a-select-option v-for="channel of channels"
                               :value="channel.channelCode" :key="channel.channelCode">{{channel.channelName}}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="请选择子账号" style="width: 300px">
            <a-select v-model="businessCode" @change="getBusinesses">
              <a-select-option v-for="business of businesses"
                               :value="business.businessCode" :key="business.businessCode">{{business.businessCode}}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="充值金额" style="width: 300px">
            <j-dict-select-tag placeholder="请选择金额" dictCode="momey" v-model="amount"/>
          </a-form-item>

        </a-form>
      </a-spin>
    </a-modal>

  </div>
</template>

<script>
  import {getAction, httpAction} from '@/api/manage'
  import JCheckbox from '@/components/jeecg/JCheckbox'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'

  export default {
    name: "ChargeBusinessModal",
    components: {
      JCheckbox,
      JMultiSelectTag
    },
    data() {
      return {
        title: "设置入金渠道",
        visible: false,
        userName: "",
        amount: "",
        businessCode: "",
        channelCode: "",
        productCode: "",
        products: [],
        channels: [],
        businesses:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {},
        url: {
          getBusinessRelatedProduct: "/v2/payBusiness/getBusinessRelatedProduct",
          getBusinessRelatedChannel: "/v2/payBusiness/getBusinessRelatedChannel",
          chargeAmount: "/v2/payBusiness/chargeAmount",
          getBusiness:"/v2/payBusiness/getBusiness"
        },
      }
    },
    mounted: function () {
    },
    methods: {
      getBusinessRelatedProduct: function () {
        let formData = [];
        formData.userName = this.userName;
        getAction(this.url.getBusinessRelatedProduct, formData).then((res) => {
          if (res.success) {
            this.products = res.result;
            this.$emit('ok');
          } else {
            this.$message.warning(res.message);
          }
        })
      },
      getBusinessRelatedChannel: function () {
        let formData = [];
        formData.productCode = this.productCode;
        formData.userName = this.userName;
        getAction(this.url.getBusinessRelatedChannel, formData).then((res) => {
          if (res.success) {
            this.channels = res.result;
            this.$emit('ok');
          } else {
            this.$message.warning(res.message);
          }
        })
      },
      getBusinesses: function(){
        let formData = [];
        formData.channelCode = this.channelCode;
        formData.productCode = this.productCode;
        formData.userName = this.userName;
        getAction(this.url.getBusiness, formData).then((res) => {
          if (res.success) {
            this.businesses = res.result;
            this.$emit('ok');
          } else {
            this.$message.warning(res.message);
          }
        })
      },
      chargeBusiness: function (record) {
        this.userName = record.username;
        this.memberType = record.memberType;
        if (this.memberType != "1") {
          alert("无权限")
          return
        }
        this.visible = true
        this.title = "子账号充值";
        this.getBusinessRelatedProduct();
      },
      handleOk() {
        let formData = {};
        formData.userName = this.userName;
        formData.amount = this.amount;
        formData.businessCode = this.businessCode;
        formData.channelCode = this.channelCode;
        formData.productCode = this.productCode;
        // 触发表单验证
        httpAction(this.url.chargeAmount, formData, "post").then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.$emit('ok');
          } else {
            this.$message.warning(res.message);
          }
        }).finally(() => {
          that.confirmLoading = false;
          that.close();
        })
      },
      close() {
        this.$emit('close');
        this.visible = false;
        this.userName = "";
        this.amount = "";
        this.businessCode = "";
        this.channelCode = "";
        this.productCode = "";
      },
      handleCancel() {
        this.close()
      },
    }
  }
</script>

<style scoped>
  .ant-modal-body {
    padding: 8px !important;
  }
</style>