import redis
from redis import WatchError
import time

RATE = 0.1
# 令牌桶的最大容量
DEFAULT = 10
# redis key 的过期时间
TIMEOUT = 60

r = redis.Redis('localhost')


def token_bucket(tokens, key):
    pipe = r.pipeline()
    while 1:
        try:
            pipe.watch('%s:available' % key)
            pipe.watch('%s:ts' % key)

            current_ts = time.time()

            # 获取令牌桶中剩余令牌
            old_tokens = pipe.get('%s:available' % key)
            if old_tokens is None:
                current_tokens = DEFAULT
            else:
                old_ts = pipe.get('%s:ts' % key)
                # 通过时间戳计算这段时间内应该添加多少令牌，如果桶满，令牌数取桶满数。
                current_tokens = float(old_tokens) + min(
                    (current_ts - float(old_ts)) * RATE,
                    DEFAULT - float(old_tokens)
                )
            # 判断剩余令牌是否足够
            if 0 <= tokens <= current_tokens:
                current_tokens -= tokens
                consumes = 1
            else:
                consumes = 0

            # 以下动作为更新 redis 中key的值，并跳出循环返回结果。
            pipe.multi()
            pipe.set('%s:available' % key, current_tokens)
            pipe.expire('%s:available' % key, TIMEOUT)
            pipe.set('%s:ts' % key, current_ts)
            pipe.expire('%s:ts' % key, TIMEOUT)
            pipe.execute()
            break
        except WatchError:
            continue
        finally:
            pipe.reset()
    return consumes


if __name__ == "__main__":
    tokens = 5
    key = '192.168.1.1'
    if token_bucket(tokens, key):
        print(True)
    else:
        print(False)
