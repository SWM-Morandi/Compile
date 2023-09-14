import sys
input = sys.stdin.readline

n = int(input())
nums = list(map(int,input().split()))
x = int(input())
nums.sort()
sum = 0

for i in range(n):
    start = i
    end = n-1
    while start < end:
        if nums[start] + nums[end] == x:
            sum +=1
            break
        else:
            end -=1
print(sum)