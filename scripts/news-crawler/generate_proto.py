#!/usr/bin/env python
"""
Python용 gRPC Proto 파일 생성 스크립트
실행: python generate_proto.py
"""

import subprocess
import sys
from pathlib import Path

def generate_proto():
    """news.proto → Python 코드 생성"""

    current_dir = Path(__file__).parent
    proto_dir = current_dir.parent.parent / "backend" / "src" / "main" / "proto"
    proto_file = proto_dir / "news.proto"
    output_dir = current_dir / "protos"

    print(f"📋 Proto 파일: {proto_file}")
    print(f"📁 출력 경로: {output_dir}")

    if not proto_file.exists():
        print(f"❌ Proto 파일을 찾을 수 없습니다: {proto_file}")
        return False

    output_dir.mkdir(exist_ok=True)

    # __init__.py 생성
    init_file = output_dir / "__init__.py"
    init_file.write_text('"""gRPC Proto generated files"""\n', encoding='utf-8')

    # protoc 명령어
    cmd = [
        sys.executable, "-m", "grpc_tools.protoc",
        f"-I{proto_dir}",
        f"--python_out={output_dir}",
        f"--grpc_python_out={output_dir}",
        str(proto_file)
    ]

    print(f"🔨 실행중...")
    print(f"🐍 Python: {sys.executable}")

    try:
        subprocess.run(cmd, check=True, capture_output=True, text=True)
        print("✅ Proto 생성 성공!")

        # ✅ import 문 자동 수정
        grpc_file = output_dir / "news_pb2_grpc.py"
        if grpc_file.exists():
            content = grpc_file.read_text(encoding='utf-8')
            # 절대 import → 상대 import로 변경
            content = content.replace("import news_pb2 as", "from . import news_pb2 as")
            grpc_file.write_text(content, encoding='utf-8')
            print("🔧 import 문 수정 완료!")

        print(f"📄 생성된 파일:")
        print(f"  - {output_dir / 'news_pb2.py'}")
        print(f"  - {output_dir / 'news_pb2_grpc.py'}")
        return True
    except subprocess.CalledProcessError as e:
        print(f"❌ Proto 생성 실패:")
        print(e.stderr)
        return False

if __name__ == "__main__":
    print("=" * 60)
    print("Python gRPC Proto 생성")
    print("=" * 60)
    generate_proto()