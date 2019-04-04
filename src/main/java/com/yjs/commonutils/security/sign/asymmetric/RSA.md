#RSA

    RSA 
        1.RSA签名验证
            https://blog.csdn.net/hanruikai/article/details/79613166
            A和B分别具有自己的公钥和私钥。A知道自己的公私钥和B的公钥，B知道自己的公私钥和A的公钥匙。
            
            流程如下：
            
            A 方：
                1. A利用hash算法对明文信息message进行加密得到hash(message)，然后利用自己对私钥进行加密得到签名，如下
                PrivateA(hash(message))=sign
                
                2. 利用B的公钥对签名和message进行加密，如下：
                PublicB(sign+message)=final
            
            B 方：
            
                1. 利用自己的私钥解密
                PrivateB（final）=sign+message
                
                2.利用A的公钥钥对签名进行解密
                PublicA（sign）=hash（message）
                
                3.利用与A相同对hash算法对message加密，比较与第二步是否相同。验证信息是否被篡改
            
        2.RSA 加密解密
            RSA加密解密相对简单，用A用私钥加密，B用A的公钥进行解密就可以