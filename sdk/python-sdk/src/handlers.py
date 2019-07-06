from typing import Callable, Dict, List, Optional

from src.utils import Context, unpack_message_from_verity

def is_problem_report(message_type: str) -> bool:
  return message_type.split('/')[3] == 'problem-report'

class Handler():
  message_type: str
  message_status: Optional[int]
  message_handler: Callable[[Dict], None]

  def __init__(self, message_type: str, message_status: Optional[int], message_handler: Callable[[Dict], None]):
    self.message_type = message_type
    self.message_status = message_status
    self.message_handler = message_handler

  def handles(self, message: Dict) -> bool:
    if self.message_status:
      return message['@type'] == self.message_type and message['status'] == self.message_status
    return message['@type'] == self.message_type

  def handle(self, message: Dict):
    self.message_handler(message)

  def __call__(self, message: Dict):
    self.message_handler(message)

class Handlers():
  handlers: List[Handler]
  default_handler: Callable[[Dict], None]
  problem_report_handler: Callable[[Dict], None]

  def __init__(self):
    self.handlers = []
    self.default_handler = None
    self.problem_report_handler = None

  def add_handler(self, message_type: str, message_status: int, message_handler: Callable[[Dict], None]):
    self.handlers.append(Handler(message_type, message_status, message_handler))

  def add_default_handler(self, handler: Callable[[Dict], None]):
    self.default_handler = handler

  def add_problem_report_handler(self, handler: Callable[[Dict], None]):
    self.problem_report_handler = handler

  async def handle_message(self, context: Context, raw_message: bytearray):
    message: Dict = await unpack_message_from_verity(context, raw_message)
    handled: bool = False

    for handler in self.handlers:
      if handler.handles(message):
        handler(message)
        handled = True

    if not handled:
      if is_problem_report(message['@type']) and self.problem_report_handler is not None:
        await self.problem_report_handler(message)
      elif self.default_handler is not None:
        self.default_handler(message)
