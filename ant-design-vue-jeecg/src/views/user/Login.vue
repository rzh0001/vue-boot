<template>
  <div class="main">
    <a-form :form="form" class="user-layout-login" ref="formLogin" id="formLogin">
      <a-tabs
        :activeKey="customActiveKey"
        :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
        @change="handleTabClick">
        <a-tab-pane key="tab1" tab="账号密码登陆">
          <a-form-item>
            <a-input
              size="large"
              v-decorator="['username',validatorRules.username,{ validator: this.handleUsernameOrEmail }]"
              type="text"
              placeholder="请输入帐户名">
              <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-input
              v-decorator="['password',validatorRules.password]"
              size="large"
              type="password"
              autocomplete="false"
              placeholder="密码">
              <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-input
              size="large"
              v-decorator="['googleCode',validatorRules.googleCode]"
              type="input"
              placeholder="请输入谷歌验证码,未绑定谷歌，则不需要输入">
              <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>
          <a-row :gutter="0">
            <a-col :span="14">
              <a-form-item>
                <a-input
                  v-decorator="['inputCode',validatorRules.inputCode]"
                  size="large"
                  type="text"
                  @change="inputCodeChange"
                  placeholder="请输入验证码">
                  <a-icon slot="prefix" v-if=" inputCodeContent==verifiedCode " type="smile" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                  <a-icon slot="prefix" v-else type="frown" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col  :span="10">
              <j-graphic-code @success="generateCode" style="float: right"></j-graphic-code>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>

      <a-form-item>
        <a-checkbox v-model="formLogin.rememberMe">自动登陆</a-checkbox>
      </a-form-item>

      <a-form-item style="margin-top:24px">
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="login-button"
          :loading="loginBtn"
          @click.stop.prevent="handleSubmit"
          :disabled="loginBtn">确定
        </a-button>
      </a-form-item>

    </a-form>

    <two-step-captcha
      v-if="requiredTwoStepCaptcha"
      :visible="stepCaptchaVisible"
      @success="stepCaptchaSuccess"
      @cancel="stepCaptchaCancel"></two-step-captcha>

    <a-modal
      :visible="showGoogle"
      :title="googleTile"
      :width="800"
      :confirmLoading="confirmLoading"
      @ok="bindGoogle"
      @cancel="closeGoogle"
      cancelText="关闭">
      <a-spin :spinning="confirmLoading">
        <div >
          <p class="login-box-msg">绑定步骤</p>
          <ol>
            <li>下载APP，APP名称为[Google Authenticator]</li>
            <li>使用Google Authenticator 进行扫描绑定</li>
            <li>输入谷歌验证码进行绑定</li>
          </ol>
          <span style='color:red' >安卓手机如果无法扫描，请通过手动输入以下验证码进行绑定：{{googleKey}}</span>

          <div class="form-group has-feedback">
            <img :src="googleSrc" >
          </div>
        </div>
        <a-form :form="form">
          <a-form-item
            label="谷歌验证码">
            <a-input placeholder="谷歌验证码" style="width:200px;"  v-model="googleCode"/>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
    </div>


</template>

<script>
  //import md5 from "md5"
  import api from '@/api'
  import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha'
  import { mapActions } from "vuex"
  import { timeFix } from "@/utils/util"
  import Vue from 'vue'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import JGraphicCode from '@/components/jeecg/JGraphicCode'
  import { putAction } from '@/api/manage'
  import { postAction } from '@/api/manage'
  import { httpAction,getAction} from '@/api/manage'
  import { encryption } from '@/utils/encryption/aesEncrypt'
  export default {
    components: {
      TwoStepCaptcha,
      JGraphicCode,
    },
    data () {
      return {
        googleTile:'添加google',
        confirmLoading: false,
        showGoogle:false,
        googleCode: '',
        googleSrc:'',
        googleKey:'',
        customActiveKey: "tab1",
        loginBtn: false,
        // login type: 0 email, 1 username, 2 telephone
        loginType: 0,
        requiredTwoStepCaptcha: false,
        stepCaptchaVisible: false,
        form: this.$form.createForm(this),
        state: {
          time: 60,
          smsSendBtn: false,
        },
        formLogin: {
          username: "",
          password: "",
          captcha: "",
          googleCode:"",
          mobile: "",
          rememberMe: true
        },
        validatorRules:{
          username:{rules: [{ required: true, message: '请输入用户名!',validator: 'click'}]},
          password:{rules: [{ required: true, message: '请输入密码!',validator: 'click'}]},
          mobile:{rules: [{validator:this.validateMobile}]},
          captcha:{rule: [{ required: true, message: '请输入验证码!'}]},
          inputCode:{rules: [{ required: true, message: '请输入验证码!'},{validator: this.validateInputCode}]}
        },
        verifiedCode:"",
        inputCodeContent:"",
        inputCodeNull:true,

        departList:[],
        departVisible:false,
        departSelected:"",
        currentUsername:"",
        validate_status:""
      }
    },
    created () {
      Vue.ls.remove(ACCESS_TOKEN)
      this.getRouterData();
      // update-begin- --- author:scott ------ date:20190225 ---- for:暂时注释，未实现登录验证码功能
//      this.$http.get('/auth/2step-code')
//        .then(res => {
//          this.requiredTwoStepCaptcha = res.result.stepCode
//        }).catch(err => {
//          console.log('2step-code:', err)
//        })
      // update-end- --- author:scott ------ date:20190225 ---- for:暂时注释，未实现登录验证码功能
      // this.requiredTwoStepCaptcha = true

    },
    methods: {
      ...mapActions([ "Login", "Logout","PhoneLogin","BindGoogle" ]),
    closeGoogle(){
        this.showGoogle=false;
    },
      getGoogle(){
        httpAction('/sys/getGoogle',null,'get').then((res)=>{
          if(res.success){
            console.log(res);
          this.googleSrc = res.result.googleUrl;
          this.googleKey = res.result.googleKey;
        }else{
          this.$message.warning(res.message);
        }
      })
      },
      // handler
      handleUsernameOrEmail (rule, value, callback) {
        const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if (regex.test(value)) {
          this.loginType = 0
        } else {
          this.loginType = 1
        }
        callback()
      },
      handleTabClick (key) {
        this.customActiveKey = key
        // this.form.resetFields()
      },
    bindGoogle(){
      this.showGoogle=false;
      let googlecode = this.googleCode;
      console.log(googlecode);
      var params = {googleCode:googlecode}
      let that = this
      that.BindGoogle(params).then((res) => {
        this.departConfirm(res)
    }).catch((err) => {
        that.requestFailed(err);
    })

     /* getAction('/sys/bind',params).then((res)=>{
        if(res.success){
        this.departConfirm(res);
      }else{
        this.$message.warning(res.message);
      }
    }).catch((err) => {
        that.requestFailed(err);
    });*/
    },
      handleSubmit () {
        let that = this
        let loginParams = {
          remember_me: that.formLogin.rememberMe
        };

        // 使用账户密码登陆
        if (that.customActiveKey === 'tab1') {
          that.form.validateFields([ 'username', 'password','googleCode','inputCode' ], { force: true }, (err, values) => {
            if (!err) {
              getAction("/sys/getEncryptedString",{}).then((res)=>{
                loginParams.username = values.username
                //loginParams.password = md5(values.password)
                loginParams.password = encryption(values.password,res.result.key,res.result.iv)
                loginParams.googleCode = values.googleCode
                that.Login(loginParams).then((res) => {
                  this.departConfirm(res)
                }).catch((err) => {
                  that.requestFailed(err);
                })
              }).catch((err) => {
                that.requestFailed(err);
              });
            }
          })
          // 使用手机号登陆
        } else {
          that.form.validateFields([ 'mobile', 'captcha' ], { force: true }, (err, values) => {
            if (!err) {
              loginParams.mobile = values.mobile
              loginParams.captcha = values.captcha
              that.PhoneLogin(loginParams).then((res) => {
                console.log(res.result);
                this.departConfirm(res)
              }).catch((err) => {
                that.requestFailed(err);
              })

            }
          })
        }
      },
      getCaptcha (e) {
        e.preventDefault();
        let that = this;
        this.form.validateFields([ 'mobile' ], { force: true },(err,values) => {
            if(!values.mobile){
              that.cmsFailed("请输入手机号");
            }else if (!err) {
              this.state.smsSendBtn = true;
              let interval = window.setInterval(() => {
                if (that.state.time-- <= 0) {
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  window.clearInterval(interval);
                }
              }, 1000);

              const hide = this.$message.loading('验证码发送中..', 0);
              let smsParams = {};
                  smsParams.mobile=values.mobile;
                  smsParams.smsmode="0";
              postAction("/sys/sms",smsParams)
                .then(res => {
                  if(!res.success){
                    setTimeout(hide, 0);
                    this.cmsFailed(res.message);
                  }
                  console.log(res);
                  setTimeout(hide, 500);
                })
                .catch(err => {
                  setTimeout(hide, 1);
                  clearInterval(interval);
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  this.requestFailed(err);
                });
            }
          }
        );
      },
      stepCaptchaSuccess () {
        this.loginSuccess()
      },
      stepCaptchaCancel () {
        this.Logout().then(() => {
          this.loginBtn = false
          this.stepCaptchaVisible = false
        })
      },
      loginSuccess () {
        this.loginBtn = false
        this.$router.push({ name: "dashboard" })
        this.$notification.success({
          message: '欢迎',
          description: `${timeFix()}，欢迎回来`,
        });
      },
      cmsFailed(err){
        this.$notification[ 'error' ]({
          message: "登录失败",
          description:err,
          duration: 4,
        });
      },
      requestFailed (err) {
        console.log(err);
        if(err.code === 302){
          this.loginBtn = false;
          this.showGoogle = true;
          this.getGoogle();
          return;
        }
        this.$notification[ 'error' ]({
          message: '登录失败',
          description: ((err.response || {}).data || {}).message || err.message || "请求出现错误，请稍后再试",
          duration: 4,
        });
        this.loginBtn = false;
      },
      validateMobile(rule,value,callback){
        if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)){
          callback();
        }else{
          callback("您的手机号码格式不正确!");
        }

      },
      validateInputCode(rule,value,callback){
        if(!value || this.verifiedCode==this.inputCodeContent){
          callback();
        }else{
          callback("您输入的验证码不正确!");
        }
      },
      generateCode(value){
        this.verifiedCode = value.toLowerCase()
      },
      inputCodeChange(e){
        this.inputCodeContent = e.target.value
        if(!e.target.value||0==e.target.value){
          this.inputCodeNull=true
        }else{
          this.inputCodeContent = this.inputCodeContent.toLowerCase()
          this.inputCodeNull=false
        }
      },
      departConfirm(res){
        console.log(res);
        if(res.success){
          let multi_depart = res.result.multi_depart
          //0:无部门 1:一个部门 2:多个部门
          if(multi_depart==0){
            this.loginSuccess()
            this.$notification.warn({
              message: '提示',
              description: `您尚未归属部门,请确认账号信息`,
              duration:3
            });
          }else if(multi_depart==2){
            this.departVisible=true
            this.currentUsername=this.form.getFieldValue("username")
            this.departList = res.result.departs
          }else {
            this.loginSuccess()
          }
        }else{
          this.requestFailed(res)
          this.Logout();
        }
      },
      departOk(){
        if(!this.departSelected){
          this.validate_status='error'
          return false
        }
       let obj = {
          orgCode:this.departSelected,
          username:this.form.getFieldValue("username")
        }
        putAction("/sys/selectDepart",obj).then(res=>{
          if(res.success){
            this.departClear()
            this.loginSuccess()
          }else{
            this.requestFailed(res)
            this.Logout().then(()=>{
              this.departClear()
            });
          }
        })
      },
      departClear(){
        this.departList=[]
        this.departSelected=""
        this.currentUsername=""
        this.departVisible=false
        this.validate_status=''
      },
      departChange(value){
        this.validate_status='success'
        this.departSelected = value
      },
    getRouterData(){
      this.$nextTick(() => {
        this.form.setFieldsValue({
        'username': this.$route.params.username
      });
    })
    },
    }
  }
</script>

<style lang="scss" scoped>

  .user-layout-login {
    label {
      font-size: 14px;
    }

    .getCaptcha {
      display: block;
      width: 100%;
      height: 40px;
    }

    .forge-password {
      font-size: 14px;
    }

    button.login-button {
      padding: 0 15px;
      font-size: 16px;
      height: 40px;
      width: 100%;
    }

    .user-login-other {
      text-align: left;
      margin-top: 24px;
      line-height: 22px;

      .item-icon {
        font-size: 24px;
        color: rgba(0,0,0,.2);
        margin-left: 16px;
        vertical-align: middle;
        cursor: pointer;
        transition: color .3s;

        &:hover {
          color: #1890ff;
        }
      }

      .register {
        float: right;
      }
    }
  }

</style>
<style>
  .valid-error .ant-select-selection__placeholder{
    color: #f5222d;
  }
</style>