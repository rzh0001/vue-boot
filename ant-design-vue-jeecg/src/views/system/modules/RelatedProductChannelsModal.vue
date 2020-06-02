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
            <a-select v-model="productCode" @change="findChannelsByRelatedProductCode"  v-decorator="['productCode', validatorRules.productCode]">
              <a-select-option v-for="product of products"
                               :value="product.productCode" :key="product.productCode">{{product.productName}}</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="请选择要关联的通道" style="width: 300px">
            <a-select v-model="channelCode" v-decorator="['channelCode', validatorRules.channelCode]">
              <a-select-option v-for="channel of channels"
                               :value="channel.channelCode" :key="channel.channelCode">{{channel.channelName}}</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="通道默认费率"  style="width: 300px" v-if="isMem">
            <j-dict-select-tag  v-model="channelRate" :triggerChange="true" placeholder="请选择通道默认费率" dictCode="rates" v-decorator="['channelRate', validatorRules.channelRate ]"/>
          </a-form-item>

          <a-form-item label="单笔金额限制" v-if="isMem">
            <a-input-group compact>
              <a-input-number placeholder="下限" v-model="lowerLimit"
                              style="width: 30%;text-align: center"
              />
              <a-input placeholder="~" disabled
                       style="width: 35px; border-left: 0px; pointer-events: none;background-color: #fff;text-align: center"/>
              <a-input-number placeholder="上限" v-model="upperLimit"
                              style="width: 35%; border-left: 0px;text-align: center"
              />
            </a-input-group>
          </a-form-item>

            <a-form-item label="子账号" style="width: 300px" v-if="isAgent">
            <a-input placeholder="子账号" style="width:200px;" v-model="businessCode" v-decorator="['businessCode', validatorRules.businessCode]"/>
          </a-form-item>
          <a-form-item label="秘钥" style="width: 300px" v-if="isAgent">
            <a-input placeholder="秘钥" style="width:200px;"v-model="businessApiKey" v-decorator="['businessApiKey', validatorRules.businessApiKey]"/>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>

  </div>
</template>

<script>
  import {getAction,httpAction} from '@/api/manage'
  import JCheckbox from '@/components/jeecg/JCheckbox'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
  export default {
    name: "RelatedProductChannelsModal",
    components: {
      JCheckbox,
      JMultiSelectTag
    },
    data() {
      return {
        title:"设置入金渠道",
        visible: false,
        isMem: false,
        isAgent:false,
        visible4Mem: false,
        lowerLimit:"",
        upperLimit:"",
        userName:"",
        memberType:"",
        businessCode:"",
        businessApiKey:"",
        channelCode:"",
        productCode:"",
        channelRate:"",
        products:[],
        channels:[],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          productCode:{rules: [{ required: true, message: '产品类型不能为空!' }]},
          channelCode:{rules: [{ required: true, message: '请选择通道名称!' }]},
          businessCode:{rules: [{ required: true, message: '子账号不能为空' }]},
          businessApiKey:{rules: [{ required: true, message: '秘钥不能为空' }]},
        },
        url: {
          findCurrentLoginAccountRelatedProduct:"/v2/payUserProduct/findCurrentLoginAccountRelatedProduct",
          findChannelsByRelatedProductCode:"/v2/payUserChannel/findChannelsByRelatedProductCode",
          getChannelsByProductCode:"/v2/payProductChannel/saveProductChannels",
          getNotRelatedChannelCodeByProductCode:"/v2/payProductChannel/getNotRelatedChannelCodeByProductCode"
        },
      }
    },
    mounted:function () {
    },
    methods: {
      findCurrentLoginAccountRelatedProduct:function(){
        getAction(this.url.findCurrentLoginAccountRelatedProduct,null).then((res)=>{
          if(res.success){
            this.products=res.result;
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        })
      },
      findChannelsByRelatedProductCode:function(){
        let formData = [];
        formData.productCode = this.productCode;
        getAction(this.url.findChannelsByRelatedProductCode,formData).then((res)=>{
          if(res.success){
            this.channels=res.result;
            this.$emit('ok');
          }else{
            this.$message.warning(res.message);
          }
        })
      },

      relatedProductChannels:function(record){
        this.title= "产品关联通道信息";
        this.findCurrentLoginAccountRelatedProduct();
        this.userName=record.username;
        this.memberType=record.memberType;
        this.visible=true
        if(this.memberType === "1"){
          //代理
          this.isAgent=true;
        }else {
          this.isMem=true;
        }

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
      close() {
        this.$emit('close');
        this.visible = false;
        this.visible4Mem = false;
        this.lowerLimit="",
        this.upperLimit="",
        this.userName="",
        this.memberType="",
        this.businessCode="",
        this.businessApiKey="",
        this.channelCode="",
        this.productCode="",
        this.channelRate="",
          this.isMem=false,
          this.isAgent=false
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