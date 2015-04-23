#include <iostream>
#include <vector>
#include <queue>
#include <pthread.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <assert.h>
#include <fcntl.h>

#define NUM_THREAD 4
#define NUM_LENGTH 200
#define MAX_EVENTS 20

#define USES_EPOLL

#ifdef USES_EPOLL
/****

（1）.创建一个epoll描述符，调用epoll_create()来完成，epoll_create()有一个整型的参数size，用来告诉内核，要创建一个有size个描述符的事件列表（集合）
int epoll_create(int size)

（2）.给描述符设置所关注的事件，并把它添加到内核的事件列表中去，这里需要调用epoll_ctl()来完成。
int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event)
这里op参数有三种，分别代表三种操作：
a. EPOLL_CTL_ADD, 把要关注的描述符和对其关注的事件的结构，添加到内核的事件列表中去
b. EPOLL_CTL_DEL，把先前添加的描述符和对其关注的事件的结构，从内核的事件列表中去除
c. EPOLL_CTL_MOD，修改先前添加到内核的事件列表中的描述符的关注的事件

（3）. 等待内核通知事件发生，得到发生事件的描述符的结构列表，该过程由epoll_wait()完成。得到事件列表后，就可以进行事件处理了。
int epoll_wait(int epfd, struct epoll_event * events, int maxevents, int timeout)


– EPOLLIN，读事件
– EPOLLOUT，写事件
– EPOLLPRI，带外数据，与select的异常事件集合对应
– EPOLLRDHUP，TCP连接对端至少写写半关闭
– EPOLLERR，错误事件
– EPOLLET，设置事件为边沿触发
– EPOLLONESHOT，只触发一次，事件自动被删除


*/
int g_epollfd;
int g_wakeFds[2];
#endif


void awake()
{
    ssize_t nWrite;
    do 
    {
        nWrite = write(g_wakeFds[1], "W", 1);
    } 
    while (nWrite == -1);
}

void awoken() 
{

    char buffer[16];
    ssize_t nRead;
    do {
        nRead = read(g_wakeFds[0], buffer, sizeof(buffer));
    } while ((nRead == -1 ) || nRead == sizeof(buffer));
}

using namespace std;
void* threadRead(void* userdata)
{
    queue<int>* q = (queue<int>*)userdata;

    struct epoll_event events[MAX_EVENTS];
    while( true )
    {
        int fds = epoll_wait(g_epollfd, events, MAX_EVENTS, 1000);
        if(fds < 0){
            printf("epoll_wait error, exit\n");
            break;
        }

        for(int i = 0; i < fds; i++){
            if( events[i].events & EPOLLIN ) // read event
            {
                printf("%s,%d/%d\n", "EPOLLIN",i,fds);
                while( !q->empty() )
                {
                    q->pop();
                    printf("removed! \n" );
                }
            }
        }
        awoken();
    }
    return userdata;
}

void* threadRun(void* userdata)
{
	queue<int>* q = (queue<int>*)userdata;
	while( true )
    {

#ifdef USES_EPOLL
        q->push( 1 );
        printf("%ld:%s\n",(long)pthread_self() ,"added!");
        awake();

#else
#endif
    	usleep(1000*500);
    }
    printf("exit thread:%ld\n",(long)pthread_self() );
	return userdata;
}

int main(int argc, char const *argv[])
{
/**
	pipe（建立管道）：
1) 头文件 #include<unistd.h>
2) 定义函数： int pipe(int filedes[2]);
3) 函数说明： pipe()会建立管道，并将文件描述词由参数filedes数组返回。
              filedes[0]为管道里的读取端
              filedes[1]则为管道的写入端。
*/
    int result = pipe(g_wakeFds);
    assert( result!=0 );

    result = fcntl(g_wakeFds[0], F_SETFL, O_NONBLOCK);
    assert(result!=0);

    result = fcntl(g_wakeFds[1], F_SETFL, O_NONBLOCK);
    assert(result!=0);

    g_epollfd = epoll_create( MAX_EVENTS );
    assert( g_epollfd > 0 );

    struct epoll_event epv = {0, {0}};
    //epv.data.ptr = userdata;
    epv.data.fd = g_wakeFds[0];
    epv.events = EPOLLIN;

    if(epoll_ctl(g_epollfd, EPOLL_CTL_ADD, g_wakeFds[0], &epv) < 0)
        printf("Event Add failed[fd=%d], evnets[%d]\n", epv.data.fd, epv.events);
    else
        printf("Event Add OK[fd=%d], op=%d, evnets[%0X]\n", epv.data.fd, EPOLL_CTL_ADD, epv.events);

    queue<int> q;
    vector<pthread_t> v;
	for (int i = 0; i < NUM_THREAD; ++i)
	{
		pthread_t tid;
		pthread_create(&tid,NULL,threadRun,&q);
		v.push_back(tid);
	}

    pthread_t tid;
    pthread_create(&tid,NULL,threadRead,&q);
    v.push_back(tid);
      
	for(vector<pthread_t>::const_iterator it = v.begin(); it < v.end(); ++it)
        pthread_join(*it,NULL);

	return 0;
}