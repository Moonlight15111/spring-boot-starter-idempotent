# 说明
```$xslt
  1. 之前的Idempotent改的一个spring boot starter
  2. 该starter是基于@Import注解来做的。个人理解，starter有两种方式：
       a. 在 resources 目录下新建 META-INF/spring.factories 添加 org.springframework.boot.autoconfigure.EnableAutoConfiguration=自动装配配置类全路径
       b. 自定义一个注解@EnableIdempotent 上面加上 @Import(自动装配配置类.class)，在 工程启动类上 添加上@EnableIdempotent注解
  3. 工程流程简述: 
       a. idempotent-starter-test 引入 spring-boot-starter-idempotent 并在 idempotent-starter-test 的工程启动类上添加了@EnableIdempotent注解
       b. Spring 处理 idempotent-starter-test 的工程启动类时解析到 @EnableIdempotent 注解
       c. Spring 处理 @EnableIdempotent 注解时发现，它被@Import注解修饰，于是开始处理 @Import 注解
       d. 通过 @Import 注解，将 IdempotentAutoConfigure 处理、纳入到IOC容器
       e. 处理 IdempotentAutoConfigure 时通过它上面的 @ComponentScan 将 spring-boot-starter-idempotent 工程中的其他对象处理、纳入到IOC容器
  4. 在IdempotentInterceptor的generateCipherText中，会有一个reset的动作。这是因为IOUtils.toByteArray会将ServletInputStream的pos推到流的尾部
     后面再读的时候，就会判定为流已经读取完毕, 从而导致无法从ServletInputStream流中获取到任何接口需要的参数, 具体参见
     AbstractMessageConverterMethodArgumentResolver.EmptyBodyCheckingHttpInputMessage的源码
     里面有个PushbackInputStream.read()，如果不进行reset，这个read返回的就是-1
```

# 流程
```$xslt
  1. 请求进来，HttpServletRequest先在HttpServletRequestReplacedFilter转为RepeatableReadHttpServletRequestWrapper
  2. 在IdempotentInterceptor的preHandle中计算密文，并通过密文判断该请求是否已经提交过
     如果已经提交过了则抛出ServiceException，提示重复提交
     如果之前没有提交过则将密文存入redis中，留给之后做判断，另外还会存一份到request中
  3. 业务流程走完，在IdempotentInterceptor的afterCompletion中取到前面计算的密文，并从redis中删除
```

# 改进
```$xslt
  1. 可能需要对MultipartHttpServletRequest(文件上传)做特殊处理，
     主要是在HttpServletRequestReplacedFilter转为RepeatableReadHttpServletRequestWrapper以及密文计算的时候
  2. 密文计算那块，可以考虑使用更多的计算项，比如：IP地址、请求头、请求方式等等
```