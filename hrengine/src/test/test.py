import base64
import datetime
import hashlib
import time

import requests


class About_Login(object):

    def login_name_encode(self, login_name):
        encodestr = base64.b64encode(login_name.encode('utf-8'))
        return str(encodestr, 'utf-8')

    def login_pwd_encode(self, pwd):
        m2 = hashlib.md5()
        m2.update(pwd.encode('utf-8'))
        return m2.hexdigest()

    def handle_pwd_forapp(self, pwd):
        pwd = self.login_pwd_encode(pwd)
        return 'a1bcd' + pwd + 'a1bcd'

    def get_cookie_for_kapost(self, qh, laravlesession, xrsf):
        return qh + ";" + "XSRF-TOKEN=" + xrsf + ";" + "laravel_session=" + laravlesession + ";"

    def replace_sign(self, param, sign_param):
        '''
        :param param: 原始入参
        :param sign_param: 需要替换的参数，如果没有返回合法的sign，则说明没到sign已经报错，所以我们把原来参数传进去即可
                          如果返回了正确的sign，则替换，继续下边的校验
                          原因：正确的用例其他参数均正确，我们只需要先跑一遍接口获取sign
        :return:
        '''
        if (sign_param == ''):
            param = param
        else:
            for key in param.keys():
                if (key == 'sign'):
                    param[key] = sign_param
        return param

    def sort_dict_for_get(self,source_dict):
        d = source_dict
        item = sorted(d, key=lambda i: i[0])
        final_str = ''
        for x in range(len(item)):
            if type(d[item[x]]) == list:
                for t in range(len(d[item[x]])):
                    single_dict = d[item[x]][t]
                    for key in single_dict:
                        final_str = final_str + (
                                str(item[x]) + '[' + str(t) + ']' + '[' + key + ']=' + str(single_dict[key])) + '&'
            else:
                final_str = final_str + str(item[x]) + '=' + str(d[item[x]]) + '&'
        final_str = final_str[:-1]
        return final_str


about_login = About_Login()

def get_cookie_token(host_name, qh, Qb, referer_id=None):
    cookies_str = qh + ';' + Qb
    r = requests.get('http://' + host_name + '/api/userinfo',
                     headers={'Host': host_name, 'Cookie': cookies_str, 'X-Requested-With': 'XMLHttpRequest',
                              'X-RefererId': referer_id})
    if 'ka' in host_name:
        try:
            if ('XSRF-TOKEN' in r.cookies.keys()):
                return r.cookies['XSRF-TOKEN'], r.cookies['laravel_session'], r.headers['csrf_token']
            else:
                return '', r.cookies['laravel_session'], r.headers['csrf_token']
        except KeyError as e:
            print(e)
            print('<b# style="color:red">请检查/var/www/ka.crm.360.cn/app/Http/Kernel.php:33左右是否有被注释的代码，如果有请去掉注释再试试，如果没有就得找人问了……</b>')

    elif 'sales' in host_name:
        # return r.cookies['XSRF-TOKEN'], r.cookies['laravel_session']
        return r.cookies['laravel_session']


def base_login(login_name, password):
    login_name = about_login.login_name_encode(login_name)
    password = about_login.login_pwd_encode(password)
    # 登陆参数组装，获取qh
    params = {"login_name": (None, login_name), "password": (None, password)}
    r = requests.post('http://crm.360.cn/login/', headers={'Host': 'crm.360.cn'}, files=params, verify=False)
    res = r.json()
    print(res["errmsg"])
    try:
        qh = str(str(r.headers['Set-Cookie']).split(";")[1].split(' ')[2])
    except:
        qh = ''
    try:
        Qb = str(str(r.headers['Set-Cookie']).split(";")[3].split(' ')[2])
    except:
        Qb = ''
    return qh, Qb


if __name__ == "__main__":

    # username = "yanghaiming_test"
    # password = "c@1234"
    # qh, Qb = base_login(username, password=password)
    # print(qh)
    # print(Qb)

    # data_callback_url = "http://127.0.0.1:8081/api/httprunner/result/summary"
    # path = "E:/data/task_id_11111.txt"
    # files = {'result':open(path,'rb')}
    # respond = requests.post(data_callback_url, files=files, headers={"Accept": "application/json, text/plain, */*"}, verify=False, timeout=10)

    s = datetime.datetime.fromtimestamp(1628161620.231).strftime("%Y-%m-%dT%H:%M:%S.%f")

    print(s[0:-3])