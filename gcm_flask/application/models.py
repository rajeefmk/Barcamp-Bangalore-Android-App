"""
models.py

App Engine datastore models

"""


from google.appengine.ext import db


class ExampleModel(db.Model):
    """Example Model"""
    example_name = db.StringProperty(required=True)
    example_description = db.TextProperty(required=True)
    added_by = db.UserProperty()
    timestamp = db.DateTimeProperty(auto_now_add=True)

class RegIDModel(db.Model):
    """Regl IDs Model"""
    regID = db.StringProperty(required=True)

class MessagesModel(db.Model):
	"""Model for storing messages sent"""
	message = db.StringProperty(required=True)
	messagetype = db.StringProperty(required=True)
	added_by = db.UserProperty(auto_current_user=True)
	sent_at = db.IntegerProperty(required=True)
