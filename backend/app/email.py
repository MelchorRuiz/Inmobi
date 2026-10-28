import logging

import resend

from app.config import settings

logger = logging.getLogger(__name__)


def send_verification_email(email: str, verification_url: str) -> None:
    if not settings.RESEND_API_KEY:
        logger.warning("RESEND_API_KEY is not configured, skipping email send")
        return

    resend.api_key = settings.RESEND_API_KEY

    try:
        resend.Emails.send({
            "from": settings.EMAIL_FROM_ADDRESS,
            "to": email,
            "subject": "Verify your email address",
            "html": _verification_html(email, verification_url),
        })
    except Exception:
        logger.exception("failed to send verification email to %s", email)


def _verification_html(email: str, verification_url: str) -> str:
    return f"""\
<html>
<body>
<p>Hello,</p>
<p>Please verify your email address by clicking the link below:</p>
<p><a href="{verification_url}">Verify my email</a></p>
<p>This link will expire in {settings.EMAIL_VERIFICATION_TOKEN_EXPIRE_HOURS} hours.</p>
<p>If you did not create an account, you can ignore this email.</p>
</body>
</html>
"""
