# AerixSimulator

![Untitled](AerixSimulator%201b658fc3ccd54d7f86d58dc66fc2fb89/Untitled.png)

# Introduction

PLC, BLE 등 기기를 활용한 개발 환경을 구축할 경우 구매하는데 발생하는 비용과 환경구성을 위한 시간이 발생하는데 이를 보완하고자 가상환경을 통한 개발환경 구축을 목표로 한다.

가상의 PLC, BLE 같은 여러 장비를 Aerix Simulator를 통해 간단하게 구성하고 MQTT 발행을 수행한다.  

프로덕션 환경에 배포하기전 실제 서드파티 영역의 센서를 시뮬레이터에 사전 구성하고 AerixG/W의 로드밸런싱의 부하정도와 스트리밍의Data PipeLine을 안정적인 흐름으로 운영하기 위한 트러블슈팅도 가능할것이다.

# Getting Started

## Step 1 — requirement

- `openjdk_version=11`
- `ktor_version=1.6.7`
- `kotlin_version=1.6.10`
- `logback_version=1.2.3`
- `exposed_version=0.37.+`
- `h2_version=2.1.+`
- `hikari_version=5.0.+`
- `jackson_version=2.13.0`
- `tls_certificates_version=1.4.0`

## Step 2 — build & deploy

- 설치 및 배포에 대해서 참고하세요 [ArxSimulator](https://www.notion.so/ArxSimulator-a9ace96e492e4d6b91d000fadfd125ff)

# API Reference

## /mqtt

### GET /packet

MQTT Module에게 모든 가상프로젝트명과 센서, 게이트웨이 등 정보를 전달합니다.

```bash
GET http://localhost:9001/mqtt/packet
Accept: application/json
```

## /project

### GET

모든 프로젝트 정보를 전달합니다.

```bash
GET http://localhost:9001/project
Accept: application/json
```

### POST

프로젝트를 생성합니다.

```bash
POST http://localhost:9001/project
Accept: application/json

{
  "name": "project name"
}
```

### PATCH

특정 ID에 해당하는 프로젝트 정보를 변경합니다.

```bash
PATCH http://localhost:9001/project
Accept: application/json
{
  "id": 1,
  "name": "project name"
}
```

### PATCH /mode

특정 ID에 해당하는 프로젝트의 시뮬레이터 동작/미동작 여부를 변경합니다.

```bash
PATCH http://localhost:9001/project/mode
Accept: application/json
{
  "id": 1,
  "mode": true or false
}
```

### DELETE /{id}

특정 ID에 해당하는 프로젝트 정보를 삭제합니다.

```bash
DELETE http://localhost:9001/project/{id}
Accept: application/json
```

## /device

### GET /{id}

특정 ID에 해당하는 디바이스 정보를 불러옵니다.

```bash
GET http://localhost:9001/device/{id}
Accept: application/json
```

### POST /gateway

가상 Gateway 정보를 생성합니다.

```bash
POST http://localhost:9001/device/gateway
Content-Type: application/json

{
  "projectID": 1,
  "ip": "192.168.0.1",
  "mqtt": "192.168.1.1",
  "port": 1883,
  "mode": true or false,
  "virtualIP": "192.168.0.2",
  "state": true or false
}
```

### PATCH /gateway

특정 ID에 해당하는 가상 Gateway 정보를 수정합니다.

```bash
PATCH http://localhost:9001/device/gateway
Content-Type: application/json

{
  "id": 1,
  "projectID": 1,
  "ip": "192.168.0.1",
  "mqtt": "192.168.1.1",
  "port": 1883,
  "mode": true,
  "virtualIP": "192.168.0.2",
  "state": true
}
```

### DELETE /gateway/{id}

특정 ID에 해당하는 가상 Gateway 정보를 삭제합니다.

```bash
DELETE http://localhost:9001/device/gateway/{id}
Accept: application/json
```

### POST /scanner

가상 스캐너를 생성합니다.

```bash
POST http://localhost:9001/device/scanner
Content-Type: application/json

{
  "projectID": 1,
  "ip": "192.168.0.99",
  "mqttIP": "192.168.0.214",
  "mqttPORT": "1883",
  "mqttTOPIC": "tharx/test",
  "name": "가상스캐너1",
  "mode": false,
  "enable": true,
  "parsing": true,
  "scanInterval": 5,
  "scanDuration": 5
}
```

### GET /scanner

모든 가상 스캐너정보를 반환합니다.

```bash
GET http://localhost:9001/device/scanner
Accept: application/json
```

### PATCH /scanner

특정 ID에 해당하는 스캐너정보를 반환합니다.

```bash
PATCH http://localhost:9001/device/scanner
Content-Type: application/json

{
  "id": 2,
  "projectID": 1,
  "ip": "192.168.0.99",
  "mqtt": "192.168.0.214",
  "port": "1883",
  "topic": "tharx/*",
  "name": "가상스캐너2",
  "mode": false,
  "enable": true,
  "parsing": true,
  "scanDuration": 5
}
```

### DELETE /scanner/{id}

특정 ID에 해당하는 스캐너정보를 삭제합니다.

```bash
DELETE http://localhost:9001/device/scanner/{id}
Accept: application/json
```

## /sensor

### GET

모든 가상센서 정보를 불러옵니다.

```bash
GET http://localhost:9001/sensor
Accept: application/json
```

### POST

가상센서를 생성합니다.

```bash
POST http://localhost:9001/sensor
Content-Type: application/json

{
  "projectID": 1,
  "scannerID": 5,
  "mac": "AA:BB:CC:DD:EE:FF",
  "provider": "AERIX",
  "model": "T435",
  "variable": true,
  "value": 2,
  "min": 0,
  "max": 10
}
```

### PATCH

특정 ID에 해당하는 가상센서정보를 변경합니다.

```bash
PATCH http://localhost:9001/sensor
Content-Type: application/json

{
  "id": 2,
  "projectID": 1,
  "scannerID": 1,
  "mac": "AA:BB:CC:DD:EE:F1",
  "provider": "AERIX",
  "model": "T435",
  "variable": true,
  "value": 9,
  "min": 0,
  "max": 10
}
```

### DELETE /{id}

특정 ID에 해당하는 가상센서를 삭제합니다.

```bash
DELETE http://localhost:9001/sensor/{id}
Accept: application/json
```
