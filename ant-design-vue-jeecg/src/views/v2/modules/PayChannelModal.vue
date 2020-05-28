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
          label="通道名称">
          <a-input placeholder="请输入通道名称" v-decorator="['channelName', validatorRules.channelName ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="通道代码">
          <a-input placeholder="请输入通道代码" v-decorator="['channelCode', validatorRules.channelCode ]" />
        </a-form-item>

        <a-form-item label="通道默认费率" :span="12"  :labelCol="labelCol" :wrapperCol="wrapperCol">
          <j-dict-select-tag  :triggerChange="true" placeholder="请选择通道默认费率" dictCode="rates" v-decorator="['channelRate', validatorRules.channelRate ]"/>
        </a-form-item>


        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="通道网关">
          <a-input placeholder="请输入通道网关" v-decorator="['channelGateway', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="通道IP白名单">
          <a-input placeholder="请输入通道IP白名单，使用,分割" v-decorator="['channelIp', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态">
          <j-dict-select-tag  v-decorator="['status', validatorRules.status]" :triggerChange="true" placeholder="请选择状态"
                              dictCode="SysStatus"/>
        </a-form-item>
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import JDictSelectTag from '@/components/dict/JDictSelectTag.vue'
  import moment from "moment"

  export default {
    name: "PayChannelModal",
    components:{
      JDictSelectTag,
    },
      data () {
      return {
        title:"操作",
        visible: false,
        rates:null,
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
        channelCode:{rules: [{ required: true, message: '请输入通道代码!' }]},
        channelName:{rules: [{ required: true, message: '请输入通道名称!' }]},
        channelRate:{rules: [{ required: true, message: '请选择通道默认费率!' }]},
          status:{rules: [{ required: true, message: '请选择状态' }]}
        },
        url: {
          add: "/v2/payChannel/add",
          edit: "/v2/payChannel/edit",
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
          this.form.setFieldsValue(pick(this.model,'channelCode','channelGateway','channelIp','channelName','channelRate','delFlag','status'))
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