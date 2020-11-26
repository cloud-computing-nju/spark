import time
import requests
import socket

TAG_URL = 'https://api.bilibili.com/x/web-interface/view/detail/tag?aid={}'
STAT_URL = 'https://api.bilibili.com/x/web-interface/archive/stat?aid={}'
VIEW_URL = 'http://api.bilibili.com/x/web-interface/view?aid={}'
SERVER_IP = 'localhost'
PORT = 8081


def socket_client(data):
    # 创建tcp套接字
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # 链接服务器
    client_socket.connect((SERVER_IP, PORT))
    print(data)
    client_socket.send(data.encode("UTF-8"))
    client_socket.close()


def get_info():
    start = 65000000
    end = 65000010 # max 100674767
    for aid in range(start, end):
        try:
            time.sleep(0.1)
            tag_url = TAG_URL.format(aid)
            view_url = VIEW_URL.format(aid)
            tag_js = requests.request('GET', tag_url).json()
            view_js = requests.request('GET', view_url).json()
            if (int(tag_js['code'])) == 0:
                tag_list = split_tag(tag_js)
            else:
                continue
            if (int(view_js['code'])) == 0:
                data_dic = split_view(view_js)
            else:
                continue
            data_dic['tag_list'] = tag_list
            socket_client(str(data_dic))
        except:
            continue


def split_tag(tag_js):
    data_list = tag_js['data']
    tag_list = []
    for data in data_list:
        tag_list.append(data['tag_name'])
    return tag_list


def split_view(view_js):
    data_list = view_js['data']
    stat = data_list['stat']
    data_dic = {'pubdate': data_list['pubdate'], 'view': stat['view'], 'danmaku': stat['danmaku'],
                'reply': stat['reply'], 'favorite': stat['favorite'], 'coin': stat['coin'],
                'like': stat['like']}  # 返回结果
    return data_dic


if __name__ == "__main__":
    get_info()
