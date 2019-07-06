import pytest
import requests

from src.transports import send_message

def test_send_message_passes():
  send_message("https://jsonplaceholder.typicode.com/posts", "Hello, world!")

def test_send_message_fails():
  with pytest.raises(requests.ConnectionError) as excinfo: 
    assert not send_message("https://247530298573290582dfgsdsgsdfg.com/posts", "Hello, world!")
  