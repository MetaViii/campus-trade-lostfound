package com.campus.service.impl;

import com.campus.dao.MessageDao;
import com.campus.dao.impl.MessageDaoImpl;
import com.campus.entity.Message;
import com.campus.service.MessageService;
import com.campus.util.ServiceException;

import java.util.List;

/**
 * 留言业务实现类。
 */
public class MessageServiceImpl implements MessageService {

    private final MessageDao messageDao = new MessageDaoImpl();

    @Override
    public List<Message> findByTarget(Integer targetType, Integer targetId) {
        return messageDao.findByTarget(targetType, targetId);
    }

    @Override
    public Message findById(Integer id) {
        return messageDao.findById(id);
    }

    @Override
    public void add(Message message) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new ServiceException("留言内容不能为空");
        }
        if (message.getContent().trim().length() > 500) {
            throw new ServiceException("留言内容不能超过 500 字");
        }
        message.setContent(message.getContent().trim());
        messageDao.insert(message);
    }

    @Override
    public void delete(Integer id) {
        messageDao.delete(id);
    }
}
